package capsrock.weather.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HourlyWeatherResponse(
        String cod,
        Integer message,
        Integer cnt,
        List<WeatherData> list
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record WeatherData(
            Long dt, //예측 시간
            Main main, //화씨 온도
            List<Weather> weather, //
            Clouds clouds,
            Wind wind,
            Rain rain,
            Snow snow,
            Integer visibility,
            Double pop,
            Sys sys,
            @JsonProperty("dt_txt")
            String dtTxt // JSON에서 "dt_txt"는 Java에서 유효한 변수명이 아니므로 명시적으로 매핑
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Main(
            Double temp,
            Double feelsLike, //체감온도도 있네 ㄷㄷ`
            Double tempMin,
            Double tempMax,
            Integer pressure,
            Integer seaLevel,
            Integer grndLevel,
            Integer humidity,
            Double tempKf
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Weather(
            Integer id,
            String main,
            String description,
            String icon
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Clouds(
            Integer all
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Wind(
            Double speed,
            Integer deg,
            Double gust
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Rain(
            @JsonProperty("1h")
            Double oneHour
    ) {}
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Snow(
            @JsonProperty("1h")
            Double oneHour
    ) {}



    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Sys(
            String pod
    ) {}
}
