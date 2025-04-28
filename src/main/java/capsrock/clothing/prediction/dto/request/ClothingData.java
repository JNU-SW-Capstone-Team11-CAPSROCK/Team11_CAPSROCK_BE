package capsrock.clothing.prediction.dto.request;

import java.time.LocalDate;

public record ClothingData(
        LocalDate date,
        MorningNoonEvening feelsLikeTemperatures,
        MorningNoonEvening correctionValues,
        MorningNoonEvening scores
){ }