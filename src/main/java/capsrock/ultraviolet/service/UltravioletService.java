package capsrock.ultraviolet.service;

import capsrock.common.dto.OpenWeatherAPIErrorResponse;
import capsrock.common.exception.InternalServerException;
import capsrock.common.exception.InvalidLatitudeLongitudeException;
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
import capsrock.ultraviolet.util.UvIndexLevelConverter;
import capsrock.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UltravioletService {

    private final UltravioletInfoClient ultravioletInfoClient;
    private final GeocodingService geocodingService;

    public UltravioletResponse getUltravioletResponse(UltravioletRequest ultravioletRequest) {

        UltravioletApiResponse ultravioletApiResponse = getUltravioletApiResponse(ultravioletRequest);

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

    private UltravioletApiResponse getUltravioletApiResponse(UltravioletRequest ultravioletRequest) {
        try{
            return  ultravioletInfoClient.getUltravioletResponse(ultravioletRequest.latitude(), ultravioletRequest.longitude());
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        }

        throw new InternalServerException("자외선 API 처리 중 예외 발생");
    }

    private void handleClientError(HttpClientErrorException e) {
        OpenWeatherAPIErrorResponse openWeatherAPIErrorResponse = e.getResponseBodyAs(OpenWeatherAPIErrorResponse.class);
        if(Objects.requireNonNull(openWeatherAPIErrorResponse).cod() == 400) {
            throw new InvalidLatitudeLongitudeException("잘못된 위도, 경도입니다.");
        }
        log.error(openWeatherAPIErrorResponse.toString());
        throw new InternalServerException("자외선 API 에러 발생");
    }

    private void handleServerError(HttpServerErrorException e) {
        OpenWeatherAPIErrorResponse openWeatherAPIErrorResponse = e.getResponseBodyAs(OpenWeatherAPIErrorResponse.class);
        log.error(Objects.requireNonNull(openWeatherAPIErrorResponse).toString());
        throw new InternalServerException("자외선 API 에러 발생");
    }


    private List<Next23HoursUltravioletLevel> getNext23HoursUltravioletLevels(UltravioletApiResponse response) {
        return response.hourly().stream()
                .sorted(Comparator.comparingLong(UltravioletApiResponse.HourlyUVData::dt))
                .map(data -> new Next23HoursUltravioletLevel(
                        TimeUtil.convertUnixTimeStamp(data.dt()),
                        UvIndexLevelConverter.convertUvIndexToLevel(data.uvi())
                ))
                .collect(Collectors.toList());
    }

    private List<NextFewDaysUltravioletLevel> getNextFewDaysUltravioletLevels(UltravioletApiResponse response) {
        return response.daily().stream()
                .sorted(Comparator.comparingLong(UltravioletApiResponse.DailyUVData::dt))
                .map(data -> new NextFewDaysUltravioletLevel(
                        TimeUtil.convertUnixTimeStamp(data.dt()),
                        TimeUtil.getDayOfWeek(TimeUtil.convertUnixTimeStamp(data.dt())),
                        Arrays.asList(1, UvIndexLevelConverter.convertUvIndexToLevel(data.uvi())
                        )
                ))
                .collect(Collectors.toList());
    }

    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = geocodingService.doReverseGeocoding(longitude,
                latitude);
        ReverseGeocodingResponse.StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }
}
