package capsrock.clothing.dto.client.request;

import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Score;
import java.time.LocalDate;

public record ClothingData(
        LocalDate date,
        FeelsLikeTemp feelsLikeTemperatures,
        Correction correctionValues,
        Score scores,
        String comment
){ }