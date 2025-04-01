package capsrock.mainPage.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WeatherApiResponse(ResponseData response) {

    public record ResponseData(
            Header header,
            Body body) {}

    public record Header(
            String resultCode,
            String resultMsg) {}

    public record Body(
            String dataType,
            Items items) {}

    public record Items(
            List<Item> item) {}

    public record Item(
            String baseDate,
            String baseTime,
            String category,
            String fcstDate,
            String fcstTime,
            String fcstValue,
            int nx,
            int ny
    ) {}
}