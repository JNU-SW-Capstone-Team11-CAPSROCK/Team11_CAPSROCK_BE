package capsrock.mainPage.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.mainPage.dto.Next23HoursWeather;
import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.dto.response.MainPageResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MainPageService {

    private final GeocodingClient geocodingClient;
    private final HourlyWeatherService hourlyWeatherService;

    public MainPageService(GeocodingClient geocodingClient,
            HourlyWeatherService hourlyWeatherService) {
        this.geocodingClient = geocodingClient;
        this.hourlyWeatherService = hourlyWeatherService;
    }


    public void getMainPage(MainPageRequest mainPageRequest) {

        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        List<Next23HoursWeather> next23HoursWeatherList = hourlyWeatherService.getHourlyWeather(
                mainPageRequest.latitude(), mainPageRequest.longitude());
    }



    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {
        ReverseGeocodingResponse response = geocodingClient.doReverseGeocoding(longitude, latitude);

        System.out.println("responseGPS = " + response);

        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }


}

