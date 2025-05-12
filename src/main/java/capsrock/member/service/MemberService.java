package capsrock.member.service;


import capsrock.auth.dto.request.LoginRequest;
import capsrock.auth.dto.request.RegisterRequest;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.service.ClothingPredictionDataService;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.member.dto.service.RecentLocationDTO;
import capsrock.member.exception.LoginFailedException;
import capsrock.member.exception.MemberDuplicatedEmailException;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.PlainPassword;
import capsrock.member.repository.MemberRepository;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.dto.response.DailyWeatherResponse.FeelsLike;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClothingPredictionDataService clothingPredictionDataService;
    private final WeatherInfoClient weatherInfoClient;

    @Transactional
    public MemberInfoDTO login(LoginRequest loginRequest) {
        Email email;
        PlainPassword plainPassword;
        try {
            email = new Email(loginRequest.email());
            plainPassword = new PlainPassword(loginRequest.password());
        } catch (IllegalArgumentException e) {
            throw new LoginFailedException();
        }

        Member foundMember = memberRepository.findByEmail(email)
                .orElseThrow(LoginFailedException::new);

        if (!passwordEncoder.matches(plainPassword.value(),
                foundMember.getEncryptedPassword().value())) {
            throw new LoginFailedException();
        }

        foundMember.updateLocation(loginRequest.latitude(), loginRequest.longitude());

        return MemberInfoDTO.from(foundMember);
    }

    @Transactional
    public MemberInfoDTO register(RegisterRequest registerRequest) {

        if (memberRepository.existsByEmail(new Email(registerRequest.email()))) {
            throw new MemberDuplicatedEmailException();
        }

        PlainPassword plainPassword = new PlainPassword(registerRequest.password());

        Member newMember = Member
                .builder()
                .email(registerRequest.email())
                .encryptedPassword(passwordEncoder.encode(plainPassword.value()))
                .nickname(registerRequest.nickname())
                .latitude(registerRequest.latitude())
                .longitude(registerRequest.longitude())
                .build();

        Member member = memberRepository.save(newMember);

        DailyWeatherResponse response = weatherInfoClient.getDailyWeatherResponse(
                registerRequest.latitude(), registerRequest.longitude(), 1);

        FeelsLike feelsLike = response.list().getFirst().feelsLike();
        FeelsLikeTemp feelsLikeTemp = new FeelsLikeTemp(feelsLike.morn(), feelsLike.day(),
                feelsLike.eve());

        clothingPredictionDataService.saveNewPredictionForRegister(member.getId(),
                registerRequest.latitude(), registerRequest.longitude(), feelsLikeTemp);

        return MemberInfoDTO.from(newMember);
    }

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(id)));

        return MemberInfoDTO.from(member);
    }

    @Transactional(readOnly = true)
    public RecentLocationDTO getRecentLocationById(Long id) {
        MemberInfoDTO memberInfoDTO = getMemberById(id);

        return new RecentLocationDTO(memberInfoDTO.recentLocation().longitude(),
                memberInfoDTO.recentLocation().latitude());
    }
}
