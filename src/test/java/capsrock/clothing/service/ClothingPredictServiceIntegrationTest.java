package capsrock.clothing.service;

import capsrock.clothing.client.ClothingGeminiClient;
import capsrock.clothing.dto.client.request.ClothingPredictionRequest;
import capsrock.clothing.dto.client.response.ClothingPredictionResponse;
import capsrock.clothing.dto.client.response.UserPrediction;
import capsrock.clothing.model.entity.ClothingPrediction;
import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Location;
import capsrock.clothing.model.vo.Score;
import capsrock.clothing.model.vo.Status;
import capsrock.clothing.repository.ClothingPredictionRepository;
import capsrock.member.dto.service.RecentLocationDTO;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import capsrock.member.service.MemberService;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.DailyWeatherResponse;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
public class ClothingPredictServiceIntegrationTest {

    @Autowired
    private ClothingPredictService clothingPredictService;

    @Autowired
    private ClothingPredictionRepository clothingPredictionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private ClothingGeminiClient clothingGeminiClient;

    @MockBean
    private MemberService memberService;

    @MockBean
    private WeatherInfoClient weatherInfoClient;

    private Member testMember;
    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setup() {
        // 테스트 시작 전 기존 데이터 정리
        clothingPredictionRepository.deleteAll();
        memberRepository.deleteAll();


        // 테스트용 Member 생성 및 저장
        testMember = Member.builder()
                .email("123@12312.com")
                .encryptedPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                .latitude(126.9779692)
                .longitude(37.566535)
                .build();

        testMember = memberRepository.save(testMember);

        // 날씨 정보 모킹
        DailyWeatherResponse.FeelsLike feelsLike = mock(DailyWeatherResponse.FeelsLike.class);
        when(feelsLike.morn()).thenReturn(15.0);
        when(feelsLike.day()).thenReturn(20.0);
        when(feelsLike.eve()).thenReturn(17.0);

// Weather 객체 생성
        DailyWeatherResponse.Weather weather = mock(DailyWeatherResponse.Weather.class);
        List<DailyWeatherResponse.Weather> weatherList = new ArrayList<>();
        weatherList.add(weather);

// Forecast 객체 생성 및 설정
        DailyWeatherResponse.Forecast forecast = mock(DailyWeatherResponse.Forecast.class);
        when(forecast.feelsLike()).thenReturn(feelsLike);
        when(forecast.weather()).thenReturn(weatherList);

// Forecast 리스트 생성
        List<DailyWeatherResponse.Forecast> forecastList = new ArrayList<>();
        forecastList.add(forecast);

// DailyWeatherResponse 모킹 및 설정
        DailyWeatherResponse weatherResponse = mock(DailyWeatherResponse.class);
        when(weatherResponse.list()).thenReturn(forecastList);

        // 위치 정보 모킹
        RecentLocationDTO locationDTO = new RecentLocationDTO(37.5, 127.0);
        when(memberService.getRecentLocationById(anyLong())).thenReturn(locationDTO);
        when(weatherInfoClient.getDailyWeatherResponse(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(weatherResponse);

        // Gemini 응답 모킹
        UserPrediction userPrediction = new UserPrediction(testMember.getId(), new Correction(1.0, 2.0, 1.5));
        ClothingPredictionResponse predictionResponse = mock(ClothingPredictionResponse.class);
        when(predictionResponse.userDataList()).thenReturn(List.of(userPrediction));

        CompletableFuture<ClothingPredictionResponse> future = CompletableFuture.completedFuture(predictionResponse);
        try {
            when(clothingGeminiClient.getPrediction(any(ClothingPredictionRequest.class)))
                    .thenReturn(future);
        } catch (Exception e) {
            // 테스트에서는 예외 발생하지 않도록 처리
        }

        // 기존 예측 데이터 생성 (COMPLETED 상태)
        for (int i = 0; i < 10; i++) {
            ClothingPrediction completedPrediction = createPrediction(testMember, Status.COMPLETED);
            clothingPredictionRepository.save(completedPrediction);
        }

        // 아카이브된 예측 데이터 추가 (5개 이상 필요)
        for (int i = 0; i < 7; i++) {
            ClothingPrediction archivedPrediction = createPrediction(testMember, Status.ARCHIVED);
            clothingPredictionRepository.save(archivedPrediction);
        }
    }

    @AfterEach
    void cleanup() {
        clothingPredictionRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("예측 서비스가 COMPLETED 상태의 예측을 ARCHIVED로 변경해야 한다")
    void shouldChangeCompletedToArchived() throws Exception {
        // given
        List<ClothingPrediction> beforePredictions = clothingPredictionRepository.findAll();
        long completedCountBefore = beforePredictions.stream()
                .filter(p -> p.getStatus().equals(Status.COMPLETED))
                .count();
        
        assertThat(completedCountBefore).isGreaterThan(0);

        // when
        clothingPredictService.predict();

        // then
        List<ClothingPrediction> afterPredictions = clothingPredictionRepository.findAll();
        long completedCountAfter = afterPredictions.stream()
                .filter(p -> p.getStatus().equals(Status.COMPLETED))
                .count();

        assertThat(completedCountAfter).isEqualTo(0);
    }

    @Test
    @DisplayName("예측 서비스가 새로운 예측을 생성해야 한다")
    void shouldCreateNewPredictions() throws Exception {
        // given
        int countBefore = clothingPredictionRepository.findAll().size();

        // when
        clothingPredictService.predict();

        // then
        int countAfter = clothingPredictionRepository.findAll().size();
        assertThat(countAfter).isGreaterThan(countBefore);

        // 새로 생성된 예측이 오늘 날짜인지 확인
        List<ClothingPrediction> todayPredictions = clothingPredictionRepository.findAll().stream()
                .filter(p -> p.getPredictedAt().equals(today))
                .toList();
        
        assertThat(todayPredictions).isNotEmpty();
    }

    @Test
    @DisplayName("충분한 아카이브 데이터가 없는 사용자의 경우 기본 보정치 0.0을 설정해야 한다")
    void shouldSetDefaultCorrectionForInsufficientData() throws Exception {
        // given
        Member newMember = Member.builder()
                .email("123@12312.com")
                .encryptedPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                .latitude(126.9779692)
                .longitude(37.566535)
                .build();
        newMember = memberRepository.save(newMember);

        // 아카이브 데이터가 MIN_PREDICTION_DATA(5)보다 적게 생성
        for (int i = 0; i < 3; i++) {
            ClothingPrediction archivedPrediction = createPrediction(newMember, Status.ARCHIVED);
            clothingPredictionRepository.save(archivedPrediction);
        }

        // when
        clothingPredictService.predict();

        // then
        Member finalNewMember = newMember;
        ClothingPrediction newMemberPrediction = clothingPredictionRepository.findAll().stream()
                .filter(p -> p.getMember().getId().equals(finalNewMember.getId()) && p.getPredictedAt().equals(today))
                .findFirst()
                .orElse(null);
        
        assertThat(newMemberPrediction).isNotNull();
        assertThat(newMemberPrediction.getCorrection().morningCorrection()).isEqualTo(0.0);
        assertThat(newMemberPrediction.getCorrection().noonCorrection()).isEqualTo(0.0);
        assertThat(newMemberPrediction.getCorrection().eveningCorrection()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Gemini 클라이언트에 올바른 데이터가 전달되어야 한다")
    void shouldPassCorrectDataToGeminiClient() throws Exception {
        // when
        clothingPredictService.predict();

        // then
        verify(clothingGeminiClient, atLeastOnce()).getPrediction(any(ClothingPredictionRequest.class));
    }

    @Test
    @DisplayName("날씨 정보가 올바르게 반영되어야 한다")
    void shouldReflectWeatherInformation() throws Exception {
        // when
        clothingPredictService.predict();

        // then
        verify(weatherInfoClient, atLeastOnce()).getDailyWeatherResponse(anyDouble(), anyDouble(), anyInt());
        
        // 오늘 생성된 예측이 올바른 FeelsLikeTemp 값을 가지고 있는지 확인
        ClothingPrediction todayPrediction = clothingPredictionRepository.findAll().stream()
                .filter(p -> p.getMember().getId().equals(testMember.getId()) && p.getPredictedAt().equals(today))
                .findFirst()
                .orElse(null);
        
        assertThat(todayPrediction).isNotNull();
        assertThat(todayPrediction.getFeelsLikeTemp().morning()).isEqualTo(15.0);
        assertThat(todayPrediction.getFeelsLikeTemp().noon()).isEqualTo(20.0);
        assertThat(todayPrediction.getFeelsLikeTemp().evening()).isEqualTo(17.0);
    }

    // 테스트용 예측 데이터 생성 헬퍼 메서드
    private ClothingPrediction createPrediction(Member member, Status status) {
        ClothingPrediction prediction = new ClothingPrediction(
                member,
                new Correction(1.0, 1.5, 0.5),
                new Location(127.0, 37.5),
                LocalDate.now().minusDays(1),
                new FeelsLikeTemp(10.0, 15.0, 12.0)
        );
        if (status.equals(Status.COMPLETED) || status.equals(Status.ARCHIVED)) {
            // PENDING에서 COMPLETED로 상태 변경
            prediction.receiveFeedback(
                    new Score(4, 4, 4),  // 더미 점수 데이터
                    "테스트 피드백",
                    LocalDateTime.now()
            );

            // ARCHIVED 상태로 변경해야 하는 경우 추가 처리
            if (status.equals(Status.ARCHIVED)) {
                prediction.changeToArchive();
            }
        }

        return prediction;

    }
}