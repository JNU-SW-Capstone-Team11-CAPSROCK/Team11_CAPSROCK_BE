// PredictionInfoDTO (새로운 DTO)
package capsrock.clothing.dto.service;

import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Score;
import capsrock.clothing.model.vo.Status;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PredictionInfoDTO(
        Long predictionId,
        Long memberId,
        LocalDate predictedAt,
        Score score,
        Correction correction,
        FeelsLikeTemp feelsLikeTemp,
        Status status,
        String comment // comment 필드 추가 (ClothingData 생성 시 필요)
) {
}