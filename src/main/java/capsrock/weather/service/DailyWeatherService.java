package capsrock.weather.service;

import capsrock.common.dto.OpenWeatherAPIErrorResponse;
import capsrock.common.exception.InternalServerException;
import capsrock.common.exception.InvalidLatitudeLongitudeException;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.service.NextFewDaysWeather;
import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.enums.WeatherEnum;
import capsrock.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyWeatherService {

    private final WeatherInfoClient weatherInfoClient;

    public List<NextFewDaysWeather> getNextFewDaysWeather(Double latitude, Double longitude, Integer days) {
//        List<Next7DaysWeather> next7DaysWeatherList = new ArrayList<>();

        DailyWeatherResponse dailyWeatherResponse = getDailyWeatherResponse(latitude, longitude, days);

//        System.out.println("dailyWeatherResponse = " + dailyWeatherResponse);

        List<NextFewDaysWeather> nextFewDaysWeatherList = dailyWeatherResponse.list().stream()
                .map(data -> {
                    String ts = TimeUtil.convertUnixTimeStamp(data.dt());
                    return new NextFewDaysWeather(
                            ts,
                            TimeUtil.getDayOfWeek(ts),
                            data.temp().max(),
                            data.temp().min(),
                            WeatherEnum.fromCode(data.weather().getFirst().id()).getId(),
                            Math.round(data.pop() * 100.0),
                            Optional.ofNullable(data.rain())
                                    .orElse(Optional.ofNullable(data.snow()).orElse(0.0))
                    );
                })
                .sorted(Comparator.comparing(NextFewDaysWeather::day))
                .toList();

        return nextFewDaysWeatherList;
    }

    private DailyWeatherResponse getDailyWeatherResponse(Double latitude, Double longitude, Integer days){
        try{
            return weatherInfoClient.getDailyWeatherResponse(latitude, longitude, days);
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
        log.error(openWeatherAPIErrorResponse.toString());
        throw new InternalServerException("날씨 API 에러 발생");
    }

    private void handleServerError(HttpServerErrorException e) {
        OpenWeatherAPIErrorResponse openWeatherAPIErrorResponse = e.getResponseBodyAs(OpenWeatherAPIErrorResponse.class);
        log.error(Objects.requireNonNull(openWeatherAPIErrorResponse).toString());
        throw new InternalServerException("날씨 API 에러 발생");
    }


}
