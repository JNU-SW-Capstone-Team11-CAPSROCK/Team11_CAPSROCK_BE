package capsrock.weather.service;

import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.service.Next23HoursWeather;
import capsrock.weather.dto.response.HourlyWeatherResponse;
import capsrock.weather.dto.response.HourlyWeatherResponse.Rain;
import capsrock.weather.dto.response.HourlyWeatherResponse.Snow;
import capsrock.weather.enums.WeatherEnum;
import capsrock.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {

    private final WeatherInfoClient weatherInfoClient;

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
