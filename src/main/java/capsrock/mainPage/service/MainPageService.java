package capsrock.mainPage.service;

import capsrock.geocoding.client.GeocodingClient;
import capsrock.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.geocoding.dto.service.AddressDTO;
import capsrock.mainPage.dto.service.Dashboard;
import capsrock.mainPage.dto.service.Next23HoursWeather;
import capsrock.mainPage.dto.service.Next7DaysWeather;
import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.dto.response.MainPageResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MainPageService {

    private final GeocodingClient geocodingClient;
    private final HourlyWeatherService hourlyWeatherService;
    private final DailyWeatherService dailyWeatherService;

    public MainPageService(GeocodingClient geocodingClient,
            HourlyWeatherService hourlyWeatherService, DailyWeatherService dailyWeatherService) {
        this.geocodingClient = geocodingClient;
        this.hourlyWeatherService = hourlyWeatherService;
        this.dailyWeatherService = dailyWeatherService;
    }


    public MainPageResponse getMainPage(MainPageRequest mainPageRequest) {

        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        List<Next23HoursWeather> next23HoursWeatherList = hourlyWeatherService.getHourlyWeather(
                mainPageRequest.latitude(), mainPageRequest.longitude());

        List<Next7DaysWeather> next7DaysWeatherList = dailyWeatherService.getNext7DaysWeather(
                mainPageRequest.latitude(), mainPageRequest.longitude());

        Dashboard dashboard = new Dashboard(addressDTO, next7DaysWeatherList.getFirst().maxTemp(),
                next7DaysWeatherList.getFirst().minTemp(),
                next23HoursWeatherList.getFirst().temp());

        return new MainPageResponse(dashboard, next23HoursWeatherList, next7DaysWeatherList);
    }


    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = geocodingClient.doReverseGeocoding(longitude, latitude);
        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }


}

