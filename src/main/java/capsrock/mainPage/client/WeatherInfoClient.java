package capsrock.mainPage.client;

import capsrock.mainPage.config.WeatherRequestConfig;

import capsrock.location.grid.dto.Grid;
import capsrock.mainPage.dto.TimeDTO;
import capsrock.mainPage.dto.response.WeatherApiResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public WeatherApiResponse getWeatherInfo(Grid grid, TimeDTO timeDTO) {
        
        //apiKey에서 '=' 때문에 이렇게 수시로 인코딩 해줘야함
        String decodedKey = weatherRequestConfig.restApiKey();
        String encodedKey = URLEncoder.encode(decodedKey, StandardCharsets.UTF_8);

        String uriString = UriComponentsBuilder
                .fromHttpUrl(weatherRequestConfig.requestUrl())
                .queryParam("serviceKey", encodedKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", timeDTO.yyyyMMdd())
                .queryParam("base_time", timeDTO.hhMM())
                .queryParam("nx", grid.x())
                .queryParam("ny", grid.y())
                .build().toUriString();

        System.out.println("uriString = " + uriString);

        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(WeatherApiResponse.class).getBody();
    }

}
