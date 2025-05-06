package capsrock.clothing.dto.service;

public record NextFewDaysClothingDTO(
    String date,
    String dayOfWeek,
    Double maxFeelsLike,
    Double minFeelsLike,
    Integer maxClothing,
    Integer minClothing
) {

}
