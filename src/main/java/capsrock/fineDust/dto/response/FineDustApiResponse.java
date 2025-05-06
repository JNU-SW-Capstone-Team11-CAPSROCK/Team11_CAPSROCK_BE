package capsrock.fineDust.dto.response;

import java.util.List;

public record FineDustApiResponse(
        Coord coord,
        List<FineDustData> list
) {
    public record Coord(
            Double lon,
            Double lat
    ) {}

    public record FineDustData(
            Long dt,
            Main main,
            Components components
    ) {}

    public record Main(
            Integer aqi  // Air Quality Index (1~5)
    ) {}

    public record Components(
            Double co,
            Double no,
            Double no2,
            Double o3,
            Double so2,
            Double pm2_5,
            Double pm10,
            Double nh3
    ) {}
}
