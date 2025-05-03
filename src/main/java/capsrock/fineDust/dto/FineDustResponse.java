package capsrock.fineDust.dto;

import java.util.List;

public record FineDustResponse(
        Coord coord,
        List<FineDustData> list
) {
    public record Coord(
            double lon,
            double lat
    ) {}

    public record FineDustData(
            long dt,
            Main main,
            Components components
    ) {}

    public record Main(
            int aqi  // Air Quality Index (1~5)
    ) {}

    public record Components(
            double co,
            double no,
            double no2,
            double o3,
            double so2,
            double pm2_5,
            double pm10,
            double nh3
    ) {}
}
