package capsrock.weather.dto.service;

public record Next23HoursWeather(
        String time,
        Integer weather,
        Double temp,
        String rainOrSnowPossibility,
        Double rainOrSnowAmount
) { }
