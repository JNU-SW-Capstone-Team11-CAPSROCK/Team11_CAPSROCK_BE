package capsrock.mainPage.service;

import capsrock.mainPage.client.WeatherInfoClient;
import capsrock.mainPage.dto.service.Next7DaysWeather;
import capsrock.mainPage.dto.response.DailyWeatherResponse;
import capsrock.mainPage.enums.WeatherEnum;
import capsrock.mainPage.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DailyWeatherService {

    private final WeatherInfoClient weatherInfoClient;

    public DailyWeatherService(WeatherInfoClient weatherInfoClient) {
        this.weatherInfoClient = weatherInfoClient;
    }

    public List<Next7DaysWeather> getNext7DaysWeather(Double latitude, Double longitude) {
//        List<Next7DaysWeather> next7DaysWeatherList = new ArrayList<>();

        DailyWeatherResponse dailyWeatherResponse = weatherInfoClient.getDailyWeatherResponse(
                latitude, longitude);

//        System.out.println("dailyWeatherResponse = " + dailyWeatherResponse);

        List<Next7DaysWeather> next7DaysWeatherList = dailyWeatherResponse.list().stream()
                .map(data -> {
                    String ts = TimeUtil.convertUnixTimeStamp(data.dt());
                    return new Next7DaysWeather(
                            ts,
                            TimeUtil.getDayOfWeek(ts),
                            data.temp().max(),
                            data.temp().min(),
                            WeatherEnum.fromCode(data.weather().getFirst().id()).getId(),
                            data.pop() * 100.0 + "%",
                            Optional.ofNullable(data.rain())
                                    .orElse(Optional.ofNullable(data.snow()).orElse(0.0))
                    );
                })
                .sorted(Comparator.comparing(Next7DaysWeather::day))
                .toList();

        return next7DaysWeatherList;
    }

}
