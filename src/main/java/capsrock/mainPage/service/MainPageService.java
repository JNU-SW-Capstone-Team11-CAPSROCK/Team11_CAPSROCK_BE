package capsrock.mainPage.service;

import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.geocoding.service.GeocodingService;
import capsrock.mainPage.dto.service.Dashboard;
import capsrock.mainPage.dto.service.Next23HoursWeather;
import capsrock.mainPage.dto.service.NextFewDaysWeather;
import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.dto.response.MainPageResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MainPageService {

    private final HourlyWeatherService hourlyWeatherService;
    private final DailyWeatherService dailyWeatherService;
    private final GeocodingService geocodingService;

    public MainPageService(HourlyWeatherService hourlyWeatherService,
            DailyWeatherService dailyWeatherService, GeocodingService geocodingService) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.dailyWeatherService = dailyWeatherService;
        this.geocodingService = geocodingService;
    }


    public MainPageResponse getMainPage(MainPageRequest mainPageRequest) {

        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        List<Next23HoursWeather> next23HoursWeatherList = hourlyWeatherService.getHourlyWeather(
                mainPageRequest.latitude(), mainPageRequest.longitude());

        List<NextFewDaysWeather> next7DaysWeatherList = dailyWeatherService.getNextFewDaysWeather(
                mainPageRequest.latitude(), mainPageRequest.longitude(), 7);

        Dashboard dashboard = new Dashboard(addressDTO, next7DaysWeatherList.getFirst().maxTemp(),
                next7DaysWeatherList.getFirst().minTemp(),
                next23HoursWeatherList.getFirst().temp());

        return new MainPageResponse(dashboard, next23HoursWeatherList, next7DaysWeatherList);
    }


    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {

        ReverseGeocodingResponse response = geocodingService.doReverseGeocoding(longitude,
                latitude);
        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }


}

