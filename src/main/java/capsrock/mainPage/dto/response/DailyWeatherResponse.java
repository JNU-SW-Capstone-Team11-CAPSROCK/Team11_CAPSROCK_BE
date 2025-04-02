package capsrock.mainPage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DailyWeatherResponse(
        City city,
        String cod,
        Double message,
        Integer cnt,
        List<Forecast> list
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record City(
            String name,
            Coord coord,
            String country,
            Integer population,
            Integer timezone
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Coord(
            Double lon,
            Double lat
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Forecast(
            Long dt,
            Long sunrise,
            Long sunset,
            Temp temp,
            FeelsLike feelsLike,
            Integer pressure,
            Integer humidity,
            List<Weather> weather,
            Double speed,
            Integer deg,
            Double gust,
            Integer clouds,
            Double rain,
            Double snow,
            Double pop
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Temp(
            Double day,
            Double min,
            Double max,
            Double night,
            Double eve,
            Double morn
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record FeelsLike(
            Double day,
            Double night,
            Double eve,
            Double morn
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Weather(
            Integer id,
            String main,
            String description,
            String icon
    ) {

    }
}


