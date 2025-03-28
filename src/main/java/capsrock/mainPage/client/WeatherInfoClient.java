package capsrock.mainPage.client;

import capsrock.mainPage.config.WeatherRequestConfig;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherInfoClient {

    private final WeatherRequestConfig weatherRequestConfig;
    private final RestClient restClient;

    public WeatherInfoClient(WeatherRequestConfig weatherRequestConfig) {
        this.weatherRequestConfig = weatherRequestConfig;
        this.restClient = RestClient.builder().build();
    }

    public void getWeatherInfo() {

//        System.out.println(weatherRequestConfig);
        var response = restClient.get()
                .uri(URI.create(weatherRequestConfig.requestUrl() + weatherRequestConfig.restApiKey()))
                .retrieve()
                .toEntity(String.class);

        System.out.println(response.getBody());
    }
}
