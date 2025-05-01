package capsrock.clothing.prediction.dto.service;

import capsrock.clothing.prediction.model.vo.Correction;
import capsrock.clothing.prediction.model.vo.FeelsLikeTemp;
import capsrock.clothing.prediction.model.vo.Score;
import java.time.LocalDate;
import lombok.Builder;


public record ClothingRecordDTO(
        LocalDate predictionDate,
        Correction correction,
        FeelsLikeTemp feelsLikeTemp,
        Score score,
        String comment
) {

    @Builder
    public static ClothingRecordDTO create(LocalDate predictionDate, Correction correction,
            FeelsLikeTemp feelsLikeTemp, Score score, String comment) {

        return new ClothingRecordDTO(predictionDate, correction, feelsLikeTemp, score, comment);
    }
}
