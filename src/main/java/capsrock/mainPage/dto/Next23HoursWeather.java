package capsrock.mainPage.dto;

public record Next23HoursWeather(
        String time,
        String weather,
        Integer temperature,
        String rainPossibility,
        String rainAmount
) { }
