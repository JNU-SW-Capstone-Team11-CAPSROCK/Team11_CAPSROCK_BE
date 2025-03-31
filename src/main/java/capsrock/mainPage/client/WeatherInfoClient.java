package capsrock.mainPage.client;

import capsrock.mainPage.config.WeatherRequestConfig;
import java.time.LocalDate;

import capsrock.mainPage.dto.Grid;
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

    public String getWeatherInfo(Grid grid) {

        String url = UriComponentsBuilder.fromHttpUrl(weatherRequestConfig.weatherRequestUrl())
                .queryParam("serviceKey", weatherRequestConfig.restApiKey())
                .queryParam("numOfRows", 1000) // 충분히 많은 데이터 요청
                .queryParam("pageNo", 1)
                .queryParam("base_date", getCurrentDate())
                .queryParam("base_time", "0500") // 당일 예보 기준 시간
                .queryParam("nx", grid.nx())
                .queryParam("ny", grid.ny())
                .queryParam("dataType", "JSON")
                .toUriString();

        return new RestTemplate().getForObject(url, String.class);
    }

    private String getCurrentDate() {
        return LocalDate.now().toString().replace("-", "");
    }

}
