package capsrock.weather.dto.service;

public record NextFewDaysWeather(
        String day,
        String dayOfWeek,
        Double maxTemp,
        Double minTemp,
        Integer weather,
        Long rainOrSnowPossibility,
        Double rainOrSnowAmount
) {
}
