package capsrock.mainPage.dto;

public record WeekWeather(
        String dayOfWeek,
        int maxTemp,
        int minTemp,
        String weather) {
}
