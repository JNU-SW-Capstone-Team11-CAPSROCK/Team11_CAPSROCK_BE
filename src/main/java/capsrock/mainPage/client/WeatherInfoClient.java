package capsrock.mainPage.client;

import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.mainPage.config.WeatherRequestConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import capsrock.mainPage.dto.Grid;
import capsrock.mainPage.dto.response.WeatherApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherInfoClient {

    private final WeatherRequestConfig weatherRequestConfig;
    private final RestClient restClient;

    public WeatherInfoClient(WeatherRequestConfig weatherRequestConfig) {
        this.weatherRequestConfig = weatherRequestConfig;
        this.restClient = RestClient.builder().build();
    }

    public WeatherApiResponse getWeatherInfo(Grid grid) {

        var uri = UriComponentsBuilder.fromHttpUrl(weatherRequestConfig.weatherRequestUrl())
                .queryParam("serviceKey", weatherRequestConfig.restApiKey())
                .queryParam("numOfRows", 1000) // 충분히 많은 데이터 요청
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", getCurrentDate())
                .queryParam("base_time", "0500") // 당일 예보 기준 시간
                .queryParam("nx", grid.nx())
                .queryParam("ny", grid.ny())
                .build(false)
                .toUriString();

        return restClient
                .get()
                .uri(uri)
                .retrieve()
                .toEntity(WeatherApiResponse.class)
                .getBody();
    }

    private String getCurrentDate() {
        return LocalDate.now().toString().replace("-", "");
    }

}
