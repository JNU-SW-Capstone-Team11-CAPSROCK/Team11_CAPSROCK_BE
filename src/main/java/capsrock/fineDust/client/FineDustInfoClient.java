package capsrock.fineDust.client;

import capsrock.fineDust.dto.FineDustResponse;
import capsrock.weather.config.WeatherRequestConfig;
import capsrock.weather.dto.response.DailyWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FineDustInfoClient {

    private final WeatherRequestConfig weatherRequestConfig;
    private final RestClient restClient = RestClient.builder().build();


    public FineDustResponse getFineDustResponse(Double latitude, Double longitude) {

        String httpUrl = weatherRequestConfig.baseRequestUrl() +
                weatherRequestConfig.airPollutionPath() +
                weatherRequestConfig.forecastPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .build().toUriString();


        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(FineDustResponse.class).getBody();
    }
}
