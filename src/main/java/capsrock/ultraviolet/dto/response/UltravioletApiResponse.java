package capsrock.ultraviolet.dto.response;

import java.util.List;

public record UltravioletApiResponse(
        Coord coord,
        List<HourlyUVData> hourly,
        List<DailyUVData> daily
) {
    public record Coord(Double lon, Double lat) {}

    public record HourlyUVData(
            Long dt,
            Double uvi
    ) {}

    public record DailyUVData(
            Long dt,
            Double uvi
    ) {}
}
