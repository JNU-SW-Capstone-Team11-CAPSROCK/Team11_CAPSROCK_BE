package capsrock.weather.service;

import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.service.NextFewDaysWeather;
import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.enums.WeatherEnum;
import capsrock.util.TimeUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyWeatherService {

    private final WeatherInfoClient weatherInfoClient;

    public List<NextFewDaysWeather> getNextFewDaysWeather(Double latitude, Double longitude, Integer days) {
//        List<Next7DaysWeather> next7DaysWeatherList = new ArrayList<>();

        DailyWeatherResponse dailyWeatherResponse = weatherInfoClient.getDailyWeatherResponse(
                latitude, longitude, days);

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
                            data.pop() * 100.0 + "%",
                            Optional.ofNullable(data.rain())
                                    .orElse(Optional.ofNullable(data.snow()).orElse(0.0))
                    );
                })
                .sorted(Comparator.comparing(NextFewDaysWeather::day))
                .toList();

        return nextFewDaysWeatherList;
    }

}
