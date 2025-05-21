package capsrock.fineDust.service;

import capsrock.fineDust.client.FineDustInfoClient;
import capsrock.fineDust.dto.request.FineDustRequest;
import capsrock.fineDust.dto.response.FineDustApiResponse;
import capsrock.fineDust.dto.response.FineDustResponse;
import capsrock.fineDust.dto.service.Dashboard;
import capsrock.fineDust.dto.service.Next23HoursFineDustLevel;
import capsrock.fineDust.dto.service.Next5DaysFineDustLevel;
import capsrock.fineDust.exception.FineDustParsingException;
import capsrock.fineDust.util.AirQualityLevelConverter;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FineDustService {

    private final FineDustInfoClient fineDustInfoClient;
    private final GeocodingService geocodingService;

    public FineDustResponse getFineDustResponse(FineDustRequest fineDustRequest) {

        FineDustApiResponse fineDustApiResponse = fineDustInfoClient.getFineDustResponse(
                fineDustRequest.latitude(), fineDustRequest.longitude());

        if (fineDustApiResponse == null || fineDustApiResponse.list() == null || fineDustApiResponse.list().isEmpty()) {
            throw new FineDustParsingException("미세먼지 API 응답 데이터가 유효하지 않습니다.");
        }

        AddressDTO addressDTO = getAddressFromGPS(fineDustRequest.longitude(),
                fineDustRequest.latitude());

        Dashboard dashboard = new Dashboard(
                addressDTO,
                AirQualityLevelConverter.convertPm10ToLevel(fineDustApiResponse.list().getFirst().components().pm10()),
                AirQualityLevelConverter.convertPm25ToLevel(fineDustApiResponse.list().getFirst().components().pm2_5())
        );

        List<Next23HoursFineDustLevel> next23HoursFineDustLevels = getNext23HoursLevels(fineDustApiResponse);

        List<Next5DaysFineDustLevel> nextFewDaysFineDustLevels = getNextFewDaysLevels(fineDustApiResponse);

        return new FineDustResponse(
                dashboard,
                next23HoursFineDustLevels,
                nextFewDaysFineDustLevels
        );
    }

    public List<Next23HoursFineDustLevel> getNext23HoursLevels(FineDustApiResponse response) {
        return response.list().stream()
                .sorted(Comparator.comparingLong(FineDustApiResponse.FineDustData::dt))
                .limit(24)
                .map(data -> new Next23HoursFineDustLevel(
                        TimeUtil.convertUnixTimeStamp(data.dt()),
                        data.main().aqi()
                ))
                .collect(Collectors.toList());
    }

    public List<Next5DaysFineDustLevel> getNextFewDaysLevels(FineDustApiResponse response) {
        return response.list().stream()
                .collect(Collectors.groupingBy(data -> {
                    String fullTime = TimeUtil.convertUnixTimeStamp(data.dt());
                    return fullTime.substring(0, 10); // 날짜만 추출
                }, TreeMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {

                    String date = entry.getKey();
                    String representativeTime = date + " 00:00:00";
                    String dayOfWeek = TimeUtil.getDayOfWeek(representativeTime);

                    Map<String, Integer> dailyLevels = entry.getValue().stream()
                            .sorted(Comparator.comparingLong(FineDustApiResponse.FineDustData::dt))
                            .collect(Collectors.toMap(
                                    data -> {
                                        String fullTime = TimeUtil.convertUnixTimeStamp(data.dt());
                                        return fullTime.substring(11, 16);
                                    },
                                    data -> data.main().aqi(),
                                    (v1, v2) -> v1,
                                    LinkedHashMap::new
                            ));

                    return new Next5DaysFineDustLevel(representativeTime, dayOfWeek, dailyLevels);
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
