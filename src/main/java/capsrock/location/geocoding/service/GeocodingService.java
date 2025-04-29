package capsrock.location.geocoding.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final GeocodingClient geocodingClient;

    public ReverseGeocodingResponse doReverseGeocoding(Double longitude, Double latitude) {
        return geocodingClient.doReverseGeocoding(longitude, latitude);
    }
}
