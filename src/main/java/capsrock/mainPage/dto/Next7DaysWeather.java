package capsrock.mainPage.dto;

public record Next7DaysWeather(
        String dayOfWeek,
        int maxTemp,
        int minTemp,
        String weather) {
}
