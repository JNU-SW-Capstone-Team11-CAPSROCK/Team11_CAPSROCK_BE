package capsrock.location.geocoding.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private final GeocodingClient geocodingClient;

    public GeocodingService(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    public ReverseGeocodingResponse doReverseGeocoding(Double longitude, Double latitude) {
        return geocodingClient.doReverseGeocoding(longitude, latitude);
    }
}
