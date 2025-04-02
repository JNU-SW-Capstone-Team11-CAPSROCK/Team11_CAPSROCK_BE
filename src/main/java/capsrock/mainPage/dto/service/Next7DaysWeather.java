package capsrock.mainPage.dto.service;

public record Next7DaysWeather(
        String day,
        String dayOfWeek,
        Double maxTemp,
        Double minTemp,
        Integer weather,
        String rainOrSnowPossibility,
        Double rainOrSnowAmount
) {
}
