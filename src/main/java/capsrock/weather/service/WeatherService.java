package capsrock.weather.service;

import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.weather.dto.service.Dashboard;
import capsrock.weather.dto.service.Next23HoursWeather;
import capsrock.weather.dto.service.NextFewDaysWeather;
import capsrock.weather.dto.request.WeatherRequest;
import capsrock.weather.dto.response.WeatherResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final HourlyWeatherService hourlyWeatherService;
    private final DailyWeatherService dailyWeatherService;
    private final GeocodingService geocodingService;

    public WeatherResponse getWeather(WeatherRequest weatherRequest) {

        AddressDTO addressDTO = getAddressFromGPS(weatherRequest.longitude(),
                weatherRequest.latitude());

        List<Next23HoursWeather> next23HoursWeatherList = hourlyWeatherService.getHourlyWeather(
                weatherRequest.latitude(), weatherRequest.longitude());

        List<NextFewDaysWeather> next7DaysWeatherList = dailyWeatherService.getNextFewDaysWeather(
                weatherRequest.latitude(), weatherRequest.longitude(), 7);

        Dashboard dashboard = new Dashboard(addressDTO, next7DaysWeatherList.getFirst().maxTemp(),
                next7DaysWeatherList.getFirst().minTemp(),
                next23HoursWeatherList.getFirst().temp());

        return new WeatherResponse(dashboard, next23HoursWeatherList, next7DaysWeatherList);
    }


    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = geocodingService.doReverseGeocoding(longitude,
                latitude);
        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }


}

