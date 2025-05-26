package capsrock.weather.dto.service;

public record NextFewHoursWeather(
        String time,
        Integer weather,
        Double temp,
        String rainOrSnowPossibility,
        Double rainOrSnowAmount
) { }
