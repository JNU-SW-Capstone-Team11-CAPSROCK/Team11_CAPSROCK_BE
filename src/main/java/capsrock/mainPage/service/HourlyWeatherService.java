package capsrock.mainPage.service;

import capsrock.mainPage.client.WeatherInfoClient;
import capsrock.mainPage.dto.service.Next23HoursWeather;
import capsrock.mainPage.dto.response.HourlyWeatherResponse;
import capsrock.mainPage.dto.response.HourlyWeatherResponse.Rain;
import capsrock.mainPage.dto.response.HourlyWeatherResponse.Snow;
import capsrock.mainPage.enums.WeatherEnum;
import capsrock.mainPage.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HourlyWeatherService {

    private final WeatherInfoClient weatherInfoClient;

    @Autowired
    public HourlyWeatherService(WeatherInfoClient weatherInfoClient) {
        this.weatherInfoClient = weatherInfoClient;
    }


    public List<Next23HoursWeather> getHourlyWeather(Double latitude, Double longitude) {

        HourlyWeatherResponse hourlyWeatherResponse = weatherInfoClient.getHourlyWeatherResponse(
                latitude, longitude);

        List<Next23HoursWeather> next23HoursWeatherList = hourlyWeatherResponse.list().stream()
                .map(weatherData -> {
                    WeatherEnum weatherEnum = WeatherEnum.fromCode(
                            weatherData.weather().getFirst().id());
                    double precipitation = Optional.ofNullable(weatherData.rain())
                            .map(Rain::oneHour)
                            .orElseGet(() -> Optional.ofNullable(weatherData.snow())
                                    .map(Snow::oneHour)
                                    .orElse(0.0));

                    return new Next23HoursWeather(
                            TimeUtil.convertUnixTimeStamp(weatherData.dtTxt()),
                            weatherEnum.getId(),
                            weatherData.main().temp(),
                            weatherData.pop() * 100.0 + "%",
                            precipitation
                    );
                })
                .sorted(Comparator.comparing(Next23HoursWeather::time))
                .toList();

        return next23HoursWeatherList;
    }


}
