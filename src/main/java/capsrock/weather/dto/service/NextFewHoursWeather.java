package capsrock.weather.dto.service;

public record NextFewHoursWeather(
        String time,
        Integer weather,
        Double temp,
        Long rainOrSnowPossibility,
        Double rainOrSnowAmount
) { }
