package capsrock.clothing.prediction.dto.client.request;

import capsrock.clothing.prediction.model.vo.Correction;
import capsrock.clothing.prediction.model.vo.FeelsLikeTemp;
import capsrock.clothing.prediction.model.vo.Score;
import java.time.LocalDate;

public record ClothingData(
        LocalDate date,
        FeelsLikeTemp feelsLikeTemperatures,
        Correction correctionValues,
        Score scores,
        String comment
){ }