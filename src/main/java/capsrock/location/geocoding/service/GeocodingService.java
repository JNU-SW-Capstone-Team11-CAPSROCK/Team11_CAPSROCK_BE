package capsrock.location.geocoding.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final GeocodingClient geocodingClient;

    public ReverseGeocodingResponse doReverseGeocoding(Double longitude, Double latitude) {
        return geocodingClient.doReverseGeocoding(longitude, latitude);
    }

    public AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = doReverseGeocoding(longitude,
                latitude);
        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }
}
