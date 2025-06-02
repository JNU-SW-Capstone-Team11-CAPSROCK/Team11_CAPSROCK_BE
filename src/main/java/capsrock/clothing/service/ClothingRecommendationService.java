package capsrock.clothing.service;


import capsrock.clothing.dto.request.ClothingPageRequest;
import capsrock.clothing.dto.response.ClothingResponse;
import capsrock.clothing.dto.service.ClothingDashboard;
import capsrock.clothing.dto.service.Next23HoursClothingDTO;
import capsrock.clothing.dto.service.NextFewDaysClothingDTO;
import capsrock.clothing.exception.PendingFeedbackNotFoundException;
import capsrock.clothing.model.vo.Correction;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothingRecommendationService {

    private final GeocodingService geocodingService;
    private final ClothingPredictionDataService clothingPredictionDataService;
    private final HourlyClothingService hourlyClothingService;
    private final DailyClothingService dailyClothingService;
    private final MemberService memberService;

    public ClothingResponse recommendClothing(MemberInfoDTO memberInfoDTO,
            ClothingPageRequest request) {

        AddressDTO addressDTO = geocodingService.getAddressFromGPS(request.longitude(),
                request.latitude());

        memberService.updateLocation(memberInfoDTO.id(), request.longitude(), request.latitude());

        Correction correction = new Correction(0.0, 0.0, 0.0);

        try {
            correction = clothingPredictionDataService.findCorrectionsByMemberIdAndPending(
                    memberInfoDTO.id());
        } catch (PendingFeedbackNotFoundException e) {
            //do nothing
        }

        List<Next23HoursClothingDTO> next23HoursClothingDTOList = hourlyClothingService.getHourlyClothing(
                request.latitude(), request.longitude(), correction);
        List<NextFewDaysClothingDTO> nextFewDaysClothingDTOList = dailyClothingService.getNextFewDaysClothing(
                request.latitude(), request.longitude(), 7);

        Next23HoursClothingDTO firstHourly = next23HoursClothingDTOList.getFirst();

        ClothingDashboard dashboard = new ClothingDashboard(
                addressDTO, firstHourly.clothingId(), firstHourly.feelsLikeTemp(),
                firstHourly.correctionValue(), firstHourly.correctedFeelsLikeTemp()
        );

        Boolean havePendingFeedback = clothingPredictionDataService.hasPendingPrediction(
                memberInfoDTO.id());

        return new ClothingResponse(havePendingFeedback, dashboard, next23HoursClothingDTOList,
                nextFewDaysClothingDTOList);
    }
}
