package capsrock.mainPage.service;

import capsrock.location.csv.dto.StnDTO;
import capsrock.location.csv.service.CoordinateToStnConverterService;
import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.mainPage.client.WeatherInfoClient;
import capsrock.mainPage.dto.request.MainPageRequest;
import org.springframework.stereotype.Service;

@Service
public class MainPageService {

    private final GeocodingClient geocodingClient;
    private final CoordinateToStnConverterService coordinateToStnConverterService;
    private final WeatherInfoClient weatherInfoClient;

    public MainPageService(GeocodingClient geocodingClient,
            CoordinateToStnConverterService coordinateToStnConverterService,
            WeatherInfoClient weatherInfoClient) {
        this.geocodingClient = geocodingClient;
        this.coordinateToStnConverterService = coordinateToStnConverterService;
        this.weatherInfoClient = weatherInfoClient;
    }

    public void getWeatherInfo(MainPageRequest mainPageRequest) {
        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(), mainPageRequest.latitude());

        StnDTO stnDTO = coordinateToStnConverterService
                .convertToStn(mainPageRequest.longitude(), mainPageRequest.latitude());

        //todo : stn 가지고 WeatherInfoClient 쓰는거 완성하기

    }

    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {
        ReverseGeocodingResponse response = geocodingClient
                .doReverseGeocoding(longitude, latitude);

        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }



}
