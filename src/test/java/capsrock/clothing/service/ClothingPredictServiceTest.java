package capsrock.clothing.service;

import static org.assertj.core.api.Assertions.*;

import capsrock.auth.dto.request.RegisterRequest;
import capsrock.auth.service.AuthService;
import capsrock.clothing.model.entity.ClothingPrediction;
import capsrock.clothing.model.vo.*;
import capsrock.clothing.repository.ClothingPredictionRepository;
import capsrock.common.security.jwt.manager.JwtManager;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import capsrock.member.service.MemberService;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("dev") // test 프로파일 사용
class ClothingPredictServiceTest {

    private static final double LONGITUDE = 126.9101584544076;
    private static final double LATITUDE = 35.179594164456965;
    private static final int TEST_MEMBER_COUNT = 3;

    private final List<String> memberTokens = new ArrayList<>();

    @Autowired
    private AuthService authService;

    @Autowired
    private ClothingPredictService clothingPredictService;

    @Autowired
    private ClothingPredictionRepository clothingPredictionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private MemberService memberService;


    @Autowired
    private JwtManager jwtManager;

    private int minPredictionData;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        Class<?> serviceClass = PredictionRequestService.class;
        Field minPredictionDataField = serviceClass.getDeclaredField("MIN_PREDICTION_DATA");

        // private 필드에 접근하기 위해 accessible 설정
        minPredictionDataField.setAccessible(true);

        // When - 필드 값 조회
        minPredictionData = (Integer) minPredictionDataField.get(null); // static이므로 null

        // 테스트 회원 생성 (각각 pending 데이터 1개씩 생성됨)
        setupTestMembers();
        // 테스트 예측 데이터 생성
        setupTestPredictions();
    }

    @AfterEach
    void tearDown() {
        clothingPredictionRepository.deleteAll();
        memberRepository.deleteAll();
        memberTokens.clear();
    }

    @Test
    @DisplayName("새벽 1시 스케줄링 통합 테스트 - 전체 프로세스 실행")
    void testPredictIntegration_FullProcess() {
        // Given - setUp에서 생성된 데이터 상태 확인
        long initialCount = clothingPredictionRepository.count();
        long initialCompletedCount = getCompletedPredictionsCount();
        long initialPendingCount = getPendingPredictionsCount();

        // When - 새벽 1시 스케줄링 실행
        clothingPredictService.predict();

        // Then - 결과 검증
        long finalCompletedCount = getCompletedPredictionsCount();
        long finalPendingCount = getPendingPredictionsCount();
        long finalArchivedCount = getArchivedPredictionsCount();
        long finalCount = clothingPredictionRepository.count();

        // 1. 기존 COMPLETED 상태가 모두 ARCHIVED로 변경되었는지 확인
        assertThat(finalCompletedCount).isEqualTo(0);

        // 2. 새로운 PENDING 예측이 생성되었는지 확인 (기존 PENDING 제외)
        long todayPendingCount = getTodayPendingPredictionsCount();
        assertThat(todayPendingCount).isGreaterThanOrEqualTo(0);

        // 3. 전체 데이터 수 증가 확인
        assertThat(finalCount).isGreaterThanOrEqualTo(initialCount);
    }

    @Test
    @DisplayName("새벽 1시 스케줄링 통합 테스트 - COMPLETED 상태 아카이브 검증")
    void testPredictIntegration_ArchiveCompleted() {
        // Given - 초기 상태 확인
        long initialCompletedCount = getCompletedPredictionsCount();
        long initialArchivedCount = getArchivedPredictionsCount();

        assertThat(initialCompletedCount).isEqualTo(2);

        // When
        clothingPredictService.predict();

        // Then
        long finalCompletedCount = getCompletedPredictionsCount();
        long finalArchivedCount = getArchivedPredictionsCount();

        // COMPLETED가 모두 ARCHIVED로 변경
        assertThat(finalCompletedCount).isEqualTo(0);
        // 기존 ARCHIVED + 변경된 COMPLETED 수 = 최종 ARCHIVED 수
        assertThat(finalArchivedCount).isEqualTo(initialArchivedCount + initialCompletedCount);
    }

    @Test
    @Transactional
    @DisplayName("새벽 1시 스케줄링 통합 테스트 - PENDING 사용자 제외 검증")
    void testPredictIntegration_ExcludePendingMembers() {
        long initialPendingCount = getPendingPredictionsCount();
        
        assertThat(initialPendingCount).isEqualTo(1);

        List<Long> memberIds = memberTokens.stream()
                .map(jwtManager::extractId)
                .toList();

        // testMember3 (index 2)의 PENDING 상태 확인
        long testMember3InitialPendingCount = getPendingCountByMemberId(memberIds.get(2));
        assertThat(testMember3InitialPendingCount).isEqualTo(1);

        // When
        clothingPredictService.predict();

        // Then - PENDING 사용자는 새로운 예측이 생성되지 않고 기존 PENDING 유지
        long testMember3FinalPendingCount = getPendingCountByMemberId(memberIds.get(2));
        assertThat(testMember3FinalPendingCount).isEqualTo(1);
    }

    @Test
    @DisplayName("새벽 1시 스케줄링 통합 테스트 - 데이터 없는 경우")
    void testPredictIntegration_NoData() {
        // Given - 모든 데이터 삭제
        clothingPredictionRepository.deleteAll();
        long initialCount = clothingPredictionRepository.count();
        assertThat(initialCount).isEqualTo(0);

        // When
        clothingPredictService.predict();

        // Then - 예외 없이 정상 종료, 데이터 여전히 없음
        long finalCount = clothingPredictionRepository.count();
        assertThat(finalCount).isEqualTo(0);
    }

    @Test
    @DisplayName("새벽 1시 스케줄링 통합 테스트 - 상태 변경 흐름 검증")
    void testPredictIntegration_StatusTransition() {
        // Given - COMPLETED 상태인 예측 데이터 찾기
        ClothingPrediction completedPrediction = clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.COMPLETED)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("COMPLETED 상태 데이터가 없습니다."));

        Long predictionId = completedPrediction.getId();

        // When
        clothingPredictService.predict();

        // Then - COMPLETED → ARCHIVED 상태 변경 확인
        ClothingPrediction updatedPrediction = clothingPredictionRepository
                .findById(predictionId)
                .orElseThrow(() -> new IllegalStateException("예측 데이터를 찾을 수 없습니다."));

        assertThat(updatedPrediction.getStatus()).isEqualTo(Status.ARCHIVED);
    }

    private void setupTestMembers() {
        List<RegisterRequest> registerRequests = List.of(
                new RegisterRequest("1@1.com", "111111", "11111111", LONGITUDE, LATITUDE),
                new RegisterRequest("2@2.com", "222222", "11111111", LONGITUDE, LATITUDE),
                new RegisterRequest("3@3.com", "333333", "11111111", LONGITUDE, LATITUDE)
        );

        registerRequests.forEach(req -> {
            // authService.registerAndGenerateToken 호출 시 pending 데이터 1개 자동 생성됨
            String token = authService.registerAndGenerateToken(req);
            memberTokens.add(token);
        });
    }

    private void setupTestPredictions() throws NoSuchFieldException, IllegalAccessException {
        List<Long> memberIds = memberTokens.stream()
                .map(jwtManager::extractId)
                .toList();

        List<Member> testMembers = memberRepository.findAllById(memberIds);

        // 첫 번째 회원에게 충분한 히스토리 데이터 생성
        setupTestPredictionsOver5Days(testMembers.getFirst());

        // 세 번째 회원은 PENDING 상태로 남겨둠 (추가 처리 없음)
        // 두 번째 회원의 데이터를 COMPLETED로 변경
        ClothingPrediction secondMemberPrediction = clothingPredictionRepository
                .findByMemberId(testMembers.get(1).getId())
                .orElseThrow(() -> new IllegalStateException("두 번째 회원의 예측 데이터를 찾을 수 없습니다."));

        secondMemberPrediction.receiveFeedback(
                new Score(0, 0, 0),
                "좋았어요",
                LocalDateTime.now().minusDays(10).minusHours(1));
        clothingPredictionRepository.save(secondMemberPrediction);
    }

    private void setupTestPredictionsOver5Days(Member member)
            throws NoSuchFieldException, IllegalAccessException {

        // 기존 pending 데이터를 completed로 변경
        ClothingPrediction pending = clothingPredictionRepository
                .findByMemberId(member.getId())
                .orElseThrow(() -> new IllegalStateException("회원의 예측 데이터를 찾을 수 없습니다."));
        
        pending.receiveFeedback(
                new Score(0, 0, 0),
                "좋았어요",
                LocalDateTime.now().minusDays(10).minusHours(1));

        pending.changeToArchive();
        clothingPredictionRepository.save(pending);

        Random random = new Random();

        // 충분한 히스토리 데이터 생성
        for (int i = 0; i < minPredictionData + 1; i++) {
            ClothingPrediction predictionI = ClothingPrediction.builder()
                    .member(member)
                    .correction(new Correction(random.nextDouble(10) - 5, random.nextDouble(10) - 5,
                            random.nextDouble(10) - 5))
                    .location(new Location(LONGITUDE, LATITUDE))
                    .predictedAt(LocalDate.now().minusDays(i + 1))
                    .feelsLikeTemp(new FeelsLikeTemp(random.nextDouble(15) + 15,
                            random.nextDouble(15) + 15, random.nextDouble(15) + 15))
                    .build();

            predictionI.receiveFeedback(
                    new Score(random.nextInt(20) - 10, random.nextInt(20) - 10,
                            random.nextInt(20) - 10),
                    "좋았어요",
                    LocalDateTime.now().minusDays(i + 1).minusHours(1)
            );
            
            // 첫 번째를 제외하고는 모두 ARCHIVED 상태로 변경
            if (i != 0) {
                predictionI.changeToArchive();
            }
            clothingPredictionRepository.save(predictionI);
        }
    }

    // 헬퍼 메서드들
    private long getCompletedPredictionsCount() {
        return clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.COMPLETED)
                .count();
    }

    private long getPendingPredictionsCount() {
        return clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.PENDING)
                .count();
    }

    private long getArchivedPredictionsCount() {
        return clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.ARCHIVED)
                .count();
    }

    private long getTodayPendingPredictionsCount() {
        return clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.PENDING &&
                        p.getPredictedAt().equals(LocalDate.now()))
                .count();
    }

    private long getPendingCountByMemberId(Long memberId) {
        return clothingPredictionRepository
                .findAll()
                .stream()
                .filter(p -> p.getStatus() == Status.PENDING &&
                        p.getMember().getId().equals(memberId))
                .count();
    }
}