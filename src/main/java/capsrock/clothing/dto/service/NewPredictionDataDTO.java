// NewPredictionDataDTO (새로운 DTO)
package capsrock.clothing.dto.service;

import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Location;

import java.time.LocalDate;

public record NewPredictionDataDTO(
        Long memberId,
        Correction predictedCorrection,
        Location location,
        LocalDate date,
        FeelsLikeTemp feelsLikeTemp
) {
}