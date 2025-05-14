package capsrock.ultraviolet.service;

import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.ultraviolet.client.UltravioletInfoClient;
import capsrock.ultraviolet.dto.request.UltravioletRequest;
import capsrock.ultraviolet.dto.response.UltravioletApiResponse;
import capsrock.ultraviolet.dto.response.UltravioletResponse;
import capsrock.ultraviolet.dto.service.Dashboard;
import capsrock.ultraviolet.dto.service.Next23HoursUltravioletLevel;
import capsrock.ultraviolet.dto.service.NextFewDaysUltravioletLevel;
import capsrock.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class UltravioletService {

    private final UltravioletInfoClient ultravioletInfoClient;
    private final GeocodingService geocodingService;

    public UltravioletResponse getUltravioletResponse(UltravioletRequest ultravioletRequest) {

        UltravioletApiResponse ultravioletApiResponse = ultravioletInfoClient.getUltravioletResponse(
                    ultravioletRequest.latitude(), ultravioletRequest.longitude()
        );

        AddressDTO addressDTO = getAddressFromGPS(
                ultravioletRequest.longitude(), ultravioletRequest.latitude());


        List<Next23HoursUltravioletLevel> next23HoursUltravioletLevels = getNext23HoursUltravioletLevels(ultravioletApiResponse);
        List<NextFewDaysUltravioletLevel> nextFewDaysUltravioletLevels = getNextFewDaysUltravioletLevels(ultravioletApiResponse);
        Dashboard dashboard = new Dashboard(addressDTO, next23HoursUltravioletLevels.getFirst().ultravioletLevel());

        return new UltravioletResponse(
                dashboard,
                next23HoursUltravioletLevels,
                nextFewDaysUltravioletLevels
        );
    }

    private List<Next23HoursUltravioletLevel> getNext23HoursUltravioletLevels(UltravioletApiResponse response) {
        return response.hourly().stream()
                .sorted(Comparator.comparingLong(UltravioletApiResponse.HourlyUVData::dt))
                .map(data -> new Next23HoursUltravioletLevel(
                        TimeUtil.convertUnixTimeStamp(data.dt()),
                        data.uvi()
                ))
                .collect(Collectors.toList());
    }

    private List<NextFewDaysUltravioletLevel> getNextFewDaysUltravioletLevels(UltravioletApiResponse response) {
        // Group hourly data by date string (yyyy-MM-dd)
        Map<String, List<UltravioletApiResponse.HourlyUVData>> hourlyByDate = response.hourly().stream()
                .collect(Collectors.groupingBy(h -> TimeUtil.convertUnixTimeStamp(h.dt()).substring(0, 10)));

        return response.daily().stream()
                .sorted(Comparator.comparingLong(UltravioletApiResponse.DailyUVData::dt))
                .map(daily -> {
                    String dateKey = TimeUtil.convertUnixTimeStamp(daily.dt()).substring(0, 10);
                    List<UltravioletApiResponse.HourlyUVData> dailyHourly = hourlyByDate.get(dateKey);
                    Map<String, Double> uvMap;
                    if (dailyHourly != null && !dailyHourly.isEmpty()) {
                        // Map each hour to its UVI value
                        uvMap = dailyHourly.stream()
                                .sorted(Comparator.comparingLong(UltravioletApiResponse.HourlyUVData::dt))
                                .collect(Collectors.toMap(
                                        data -> TimeUtil.convertUnixTimeStamp(data.dt()).substring(11),
                                        UltravioletApiResponse.HourlyUVData::uvi,
                                        (existing, replacement) -> existing,
                                        LinkedHashMap::new
                                ));
                    } else {
                        // No hourly data: provide default times
                        uvMap = Map.of(
                                "00:00", 0.0,
                                TimeUtil.convertUnixTimeStamp(daily.dt()).substring(11), daily.uvi()
                        );
                    }
                    return new NextFewDaysUltravioletLevel(
                            TimeUtil.convertUnixTimeStamp(daily.dt()),
                            TimeUtil.getDayOfWeek(TimeUtil.convertUnixTimeStamp(daily.dt())),
                            uvMap);
                })
                .collect(Collectors.toList());
    }

    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = geocodingService.doReverseGeocoding(longitude,
                latitude);
        ReverseGeocodingResponse.StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }
}
