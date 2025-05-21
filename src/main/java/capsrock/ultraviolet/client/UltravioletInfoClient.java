package capsrock.ultraviolet.client;

import capsrock.ultraviolet.config.UltravioletRequestConfig;
import capsrock.ultraviolet.dto.response.UltravioletApiResponse;
import capsrock.ultraviolet.exception.ExternalUltravioletApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UltravioletInfoClient {

    private final RestClient restClient = RestClient.builder().build();
    private final UltravioletRequestConfig ultravioletRequestConfig;

    public UltravioletApiResponse getUltravioletResponse(Double latitude, Double longitude) {
        try {
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
        }catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ExternalUltravioletApiException("자외선 API 응답 실패: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ExternalUltravioletApiException("자외선 API 호출 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
