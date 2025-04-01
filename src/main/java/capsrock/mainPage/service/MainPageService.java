package capsrock.mainPage.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.mainPage.client.WeatherInfoClient;
import capsrock.mainPage.dto.Dashboard;
import capsrock.mainPage.dto.Grid;
import capsrock.mainPage.dto.TodayWeather;
import capsrock.mainPage.dto.WeekWeather;
import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.dto.response.MainPageResponse;
import capsrock.mainPage.dto.response.WeatherApiResponse;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainPageService {

    private final GeocodingClient geocodingClient;
    private final WeatherInfoClient weatherInfoClient;

    public MainPageService(GeocodingClient geocodingClient,
            WeatherInfoClient weatherInfoClient
            ) {
        this.geocodingClient = geocodingClient;
        this.weatherInfoClient = weatherInfoClient;
    }

    public MainPageResponse getWeatherInfo(MainPageRequest mainPageRequest){
        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        WeatherApiResponse weatherApiResponse = weatherInfoClient.getWeatherInfo(
                Grid.convertToGrid(mainPageRequest.longitude(), mainPageRequest.longitude()));

        List<TodayWeather> todayWeathers = parseTodayWeatherInfo(
                weatherApiResponse.response().body().items().item());

        List<WeekWeather> weekWeathers = parseWeekWeatherData(
                weatherApiResponse.response().body().items().item());

        TodayWeather today = todayWeathers.getFirst();
        WeekWeather week = weekWeathers.getFirst();

        return new MainPageResponse(
                new Dashboard(week.maxTemp(), week.minTemp(), today.temp()),
                todayWeathers,
                weekWeathers);

    }

    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {
        ReverseGeocodingResponse response = geocodingClient
                .doReverseGeocoding(longitude, latitude);

        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }

    private List<TodayWeather> parseTodayWeatherInfo(List<WeatherApiResponse.Item> items) {

        Map<String, Map<String, String>> groupedByTime = items.stream()
                .filter(item -> item.fcstDate().equals(getCurrentDate()))
                .filter(item -> List.of("TMP", "SKY", "PTY").contains(item.category()))
                .collect(Collectors.groupingBy(
                        WeatherApiResponse.Item::fcstTime,
                        Collectors.toMap(WeatherApiResponse.Item::category, WeatherApiResponse.Item::fcstValue)
                ));

        return groupedByTime.entrySet().stream()
                .map(entry -> new TodayWeather(
                        entry.getKey(),
                        getWeatherDescription(entry.getValue().get("SKY"), entry.getValue().get("PTY")),
                        Integer.parseInt(entry.getValue().getOrDefault("TMP", "0"))
                ))
                .collect(Collectors.toList());
    }

    private String getCurrentDate() {
        return LocalDate.now().toString().replace("-", "");
    }

    private List<WeekWeather> parseWeekWeatherData(List<WeatherApiResponse.Item> items) {
        Map<String, Map<String, String>> groupedByDate = items.stream()
                .filter(item -> List.of("TMN", "TMX", "SKY", "PTY").contains(item.category()))
                .collect(Collectors.groupingBy(
                        WeatherApiResponse.Item::fcstDate,
                        Collectors.toMap(WeatherApiResponse.Item::category, WeatherApiResponse.Item::fcstValue)
                ));

        return groupedByDate.entrySet().stream()
                .map(entry -> new WeekWeather(
                        convertToDayOfWeek(entry.getKey()),
                        Integer.parseInt(entry.getValue().getOrDefault("TMX", "0")),
                        Integer.parseInt(entry.getValue().getOrDefault("TMN", "0")),
                        getWeatherDescription(entry.getValue().get("SKY"), entry.getValue().get("PTY"))
                ))
                .collect(Collectors.toList());
    }

    private String convertToDayOfWeek(String fcstDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(fcstDate, formatter);

        // LocalDate 객체에서 요일을 구하고, 요일 이름을 반환
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.KOREAN);
    }

    private static String getWeatherDescription(String sky, String pty) {
        if (pty != null && !pty.equals("0")) {
            return switch (pty) {
                case "1" -> "없음";
                case "2" -> "비/눈";
                case "3" -> "눈";
                case "4" -> "소나기";
                default -> "알 수 없음";
            };
        }
        return switch (sky) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "알 수 없음";
        };
    }

}
