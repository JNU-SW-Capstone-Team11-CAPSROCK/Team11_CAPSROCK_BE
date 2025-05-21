package capsrock.fineDust.client;

import capsrock.fineDust.config.FineDustRequestConfig;
import capsrock.fineDust.dto.response.FineDustApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FineDustInfoClient {

    private final FineDustRequestConfig fineDustRequestConfig;
    private final RestClient restClient = RestClient.builder().build();

    public FineDustApiResponse getFineDustResponse(Double latitude, Double longitude) {

        String httpUrl = fineDustRequestConfig.baseRequestUrl() +
                fineDustRequestConfig.airPollutionPath();

        String uriString = UriComponentsBuilder
                .fromHttpUrl(httpUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", fineDustRequestConfig.restApiKey())
                .queryParam("mode", "JSON")
                .build()
                .toUriString();

        return restClient
                .get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(FineDustApiResponse.class)
                .getBody();

    }
}
