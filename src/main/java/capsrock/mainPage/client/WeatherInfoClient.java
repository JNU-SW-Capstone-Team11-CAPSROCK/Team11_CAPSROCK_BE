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

        var baseUrl = weatherRequestConfig.weatherRequestUrl();
        var serviceKey = weatherRequestConfig.restApiKey();
        var baseDate = getCurrentDate();
        var baseTime = "0500";
        var nx = grid.nx();
        var ny = grid.ny();

        var uri = baseUrl + "?"
                + "serviceKey=" + serviceKey
                + "&numOfRows=1000"
                + "&pageNo=1"
                + "&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        System.out.println(uri);
        System.out.println(restClient.get().uri(uri).retrieve().toEntity(String.class));

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
