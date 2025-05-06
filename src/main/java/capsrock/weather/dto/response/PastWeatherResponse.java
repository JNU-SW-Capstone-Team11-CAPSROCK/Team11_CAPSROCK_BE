package capsrock.weather.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PastWeatherResponse(
        String message,
        String cod,
        Integer cityId,
        Double calctime,
        Integer cnt,
        List<WeatherData> list
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record WeatherData(
            Long dt,
            Main main,
            Wind wind,
            Clouds clouds,
            List<Weather> weather,
            Rain rain
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Main(
            Double temp,
            Double feelsLike,
            Integer pressure,
            Integer humidity,
            Double tempMin,
            Double tempMax
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Wind(
            Double speed,
            Integer deg
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Clouds(
            Integer all
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Weather(
            Integer id,
            String main,
            String description,
            String icon
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Rain(
            @JsonProperty("1h")
            Double oneHour
    ) {}
}