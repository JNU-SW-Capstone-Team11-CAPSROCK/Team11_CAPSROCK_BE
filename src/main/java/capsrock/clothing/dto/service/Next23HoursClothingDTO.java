package capsrock.clothing.dto.service;

public record Next23HoursClothingDTO(
        String time,
        Integer clothingId,
        Double feelsLikeTemp,
        Double correctionValue,
        Double correctedFeelsLikeTemp
) {

}
