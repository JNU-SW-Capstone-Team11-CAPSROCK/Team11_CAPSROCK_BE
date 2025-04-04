package capsrock.mainPage.client;

import capsrock.mainPage.config.WeatherRequestConfig;

import capsrock.mainPage.dto.response.DailyWeatherResponse;
import capsrock.mainPage.dto.response.HourlyWeatherResponse;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WeatherInfoClient {

    private final WeatherRequestConfig weatherRequestConfig;
    private final RestClient restClient;

    public WeatherInfoClient(WeatherRequestConfig weatherRequestConfig) {
        this.weatherRequestConfig = weatherRequestConfig;
        this.restClient = RestClient.builder().build();
    }

    public HourlyWeatherResponse getHourlyWeatherResponse(Double latitude, Double longitude) {

        String httpUrl = weatherRequestConfig.baseRequestUrl() + weatherRequestConfig.forecastPath()
                + weatherRequestConfig.hourlyPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .queryParam("cnt", "23")
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

    public DailyWeatherResponse getDailyWeatherResponse(Double latitude, Double longitude) {
        String httpUrl = weatherRequestConfig.baseRequestUrl() + weatherRequestConfig.forecastPath()
                + weatherRequestConfig.dailyPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .queryParam("cnt", "7")
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

}
