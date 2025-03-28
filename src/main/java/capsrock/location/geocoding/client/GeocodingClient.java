package capsrock.location.geocoding.client;

import capsrock.location.geocoding.config.GeocodingRequestConfig;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeocodingClient {
    private final GeocodingRequestConfig geocodingRequestConfig;
    private final RestClient restClient;

    public GeocodingClient(GeocodingRequestConfig geocodingRequestConfig) {
        this.geocodingRequestConfig = geocodingRequestConfig;
        this.restClient = RestClient.builder().build();
    }

    public ReverseGeocodingResponse doReverseGeocoding(Double longitude, Double latitude) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(geocodingRequestConfig.requestUrl())
                .queryParam("service", "address")
                .queryParam("request", "GetAddress")
                .queryParam("key", geocodingRequestConfig.restApiKey())
                .queryParam("errorFormat", "json")
                .queryParam("point", longitude + "," + latitude)
                .queryParam("type", "PARCEL")
                .queryParam("zipcode", false)
                .queryParam("simple", false)
                .queryParam("crs", "EPSG:4326")
                .build()
                .toUri();

        return restClient
                .get()
                .uri(uri)
                .retrieve()
                .toEntity(ReverseGeocodingResponse.class)
                .getBody();

    }
}
