package capsrock.weather.client;

import capsrock.weather.config.WeatherRequestConfig;

import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.dto.response.HourlyWeatherResponse;
import java.net.URI;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class WeatherInfoClient {

    private final WeatherRequestConfig weatherRequestConfig;
    private final RestClient restClient = RestClient.builder().build();

    public HourlyWeatherResponse getHourlyWeatherResponse(Double latitude, Double longitude, Integer hours) {
        String httpUrl = weatherRequestConfig.baseRequestUrl() + weatherRequestConfig.forecastPath()
                + weatherRequestConfig.hourlyPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .queryParam("cnt", hours)
                .queryParam("lang", "kr")
                .queryParam("units", "metric")
                .build().toUriString();

        System.out.println("uriString = " + uriString);

        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(HourlyWeatherResponse.class).getBody();
    }

    public DailyWeatherResponse getDailyWeatherResponse(Double latitude, Double longitude, Integer days) {
        String httpUrl = weatherRequestConfig.baseRequestUrl() + weatherRequestConfig.forecastPath()
                + weatherRequestConfig.dailyPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .queryParam("cnt", days)
                .queryParam("lang", "kr")
                .queryParam("units", "metric")
                .build().toUriString();

        System.out.println("uriString = " + uriString);

        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(DailyWeatherResponse.class).getBody();
    }
//
//    public PastWeatherResponse getPastWeatherResponse(Double latitude, Double longitude, String startUTC, String endUTC) {
//        String httpUrl = weatherRequestConfig.historyBaseRequestUrl();
//        String uriString = UriComponentsBuilder
//                .fromHttpUrl(httpUrl)
//                .queryParam("lat", latitude)
//                .queryParam("lon", longitude)
//                .queryParam("type", "hour")
//                .queryParam("appid", weatherRequestConfig.restApiKey())
//                .queryParam("start", startUTC)
//                .queryParam("end", endUTC)
//                .build().toUriString();
//
//        System.out.println("uriString = " + uriString);
//
//        return restClient
//                .get()
//                .uri(URI.create(uriString))
//                .retrieve()
//                .toEntity(PastWeatherResponse.class).getBody();
//    }

}
