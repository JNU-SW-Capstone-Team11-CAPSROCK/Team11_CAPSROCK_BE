package capsrock.weather.service;

import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.WeatherNowResponse;
import capsrock.weather.dto.service.Dashboard;
import capsrock.weather.dto.service.NextFewHoursWeather;
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
    private final WeatherInfoClient weatherInfoClient;

    public WeatherResponse getWeather(WeatherRequest weatherRequest) {

        AddressDTO addressDTO = geocodingService.getAddressFromGPS(weatherRequest.longitude(),
                weatherRequest.latitude());

        List<NextFewHoursWeather> nextFewHoursWeatherList = hourlyWeatherService.getHourlyWeather(
                weatherRequest.latitude(), weatherRequest.longitude(), 23);

        List<NextFewDaysWeather> next7DaysWeatherList = dailyWeatherService.getNextFewDaysWeather(
                weatherRequest.latitude(), weatherRequest.longitude(), 7);

        Dashboard dashboard = new Dashboard(addressDTO, next7DaysWeatherList.getFirst().maxTemp(),
                next7DaysWeatherList.getFirst().minTemp(),
                nextFewHoursWeatherList.getFirst().temp());

        return new WeatherResponse(dashboard, nextFewHoursWeatherList, next7DaysWeatherList);
    }

    public WeatherNowResponse getWeatherNow(WeatherRequest weatherRequest) {
        List<NextFewHoursWeather> nextFewHoursWeatherList = hourlyWeatherService.getHourlyWeather(
                weatherRequest.latitude(), weatherRequest.longitude(), 1);

        return new WeatherNowResponse(nextFewHoursWeatherList.getFirst().weather());
    }

}

