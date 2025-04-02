package capsrock.mainPage.dto;

public record Next23HoursWeather(
        String time,
        Integer weather,
        Double temperature,
        String rainOrSnowPossibility,
        Double rainOrSnowAmount
) { }
