package capsrock.weather.service;

import capsrock.common.dto.OpenWeatherAPIErrorResponse;
import capsrock.common.exception.InternalServerException;
import capsrock.common.exception.InvalidLatitudeLongitudeException;
import capsrock.ultraviolet.service.UltravioletService;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.service.Next23HoursWeather;
import capsrock.weather.dto.response.HourlyWeatherResponse;
import capsrock.weather.dto.response.HourlyWeatherResponse.Rain;
import capsrock.weather.dto.response.HourlyWeatherResponse.Snow;
import capsrock.weather.enums.WeatherEnum;
import capsrock.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {

    private final WeatherInfoClient weatherInfoClient;
    private static final Logger logger = LoggerFactory.getLogger(UltravioletService.class);


    public List<Next23HoursWeather> getHourlyWeather(Double latitude, Double longitude) {

        HourlyWeatherResponse hourlyWeatherResponse = getHourlyWeatherResponse(latitude, longitude);

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

    private HourlyWeatherResponse getHourlyWeatherResponse(Double latitude, Double longitude) {
        try {
            return weatherInfoClient.getHourlyWeatherResponse(latitude, longitude);
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        }

        throw new InternalServerException("날씨 API 처리 중 에러 발생");
    }

    private void handleClientError(HttpClientErrorException e) {
        OpenWeatherAPIErrorResponse openWeatherAPIErrorResponse = e.getResponseBodyAs(OpenWeatherAPIErrorResponse.class);
        if(Objects.requireNonNull(openWeatherAPIErrorResponse).cod() == 400) {
            throw new InvalidLatitudeLongitudeException("잘못된 위도, 경도입니다.");
        }
        logger.error(openWeatherAPIErrorResponse.toString());
        throw new InternalServerException("날씨 API 에러 발생");
    }

    private void handleServerError(HttpServerErrorException e) {
        OpenWeatherAPIErrorResponse openWeatherAPIErrorResponse = e.getResponseBodyAs(OpenWeatherAPIErrorResponse.class);
        logger.error(Objects.requireNonNull(openWeatherAPIErrorResponse).toString());
        throw new InternalServerException("날씨 API 에러 발생");
    }

}
