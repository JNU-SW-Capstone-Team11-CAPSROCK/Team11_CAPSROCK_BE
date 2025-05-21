package capsrock.fineDust.client;

import capsrock.fineDust.config.FineDustRequestConfig;
import capsrock.fineDust.dto.response.FineDustApiResponse;
import capsrock.fineDust.exception.ExternalFineDustApiException;
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
        try {
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

        }  catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ExternalFineDustApiException("미세먼지 API 응답 실패: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ExternalFineDustApiException("미세먼지 API 호출 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
