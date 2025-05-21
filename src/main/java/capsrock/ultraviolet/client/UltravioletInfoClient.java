package capsrock.ultraviolet.client;

import capsrock.ultraviolet.config.UltravioletRequestConfig;
import capsrock.ultraviolet.dto.response.UltravioletApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UltravioletInfoClient {

    private final RestClient restClient = RestClient.builder().build();
    private final UltravioletRequestConfig ultravioletRequestConfig;

    public UltravioletApiResponse getUltravioletResponse(Double latitude, Double longitude) {

        String httpUrl = ultravioletRequestConfig.baseRequestUrl();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", ultravioletRequestConfig.restApiKey())
                .queryParam("exclude", "minutely,alerts,current") // daily만 받기
                .queryParam("units", "metric")
                .build()
                .toUriString();

        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(UltravioletApiResponse.class).getBody();

        }
}
