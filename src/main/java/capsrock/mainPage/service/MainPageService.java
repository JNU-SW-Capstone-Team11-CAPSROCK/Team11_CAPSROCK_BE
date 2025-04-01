package capsrock.mainPage.service;

import capsrock.location.geocoding.client.GeocodingClient;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse;
import capsrock.location.geocoding.dto.response.ReverseGeocodingResponse.StructureData;
import capsrock.location.geocoding.dto.service.AddressDTO;
import capsrock.location.grid.util.GpsToGridConverter;
import capsrock.mainPage.client.WeatherInfoClient;
import capsrock.mainPage.dto.Dashboard;
import capsrock.location.grid.dto.Grid;
import capsrock.mainPage.dto.TodayWeather;
import capsrock.mainPage.dto.WeekWeather;
import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.dto.response.MainPageResponse;
import capsrock.mainPage.dto.response.WeatherApiResponse;
import capsrock.mainPage.dto.response.WeatherApiResponse.Item;
import capsrock.mainPage.util.TimeUtil;
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
                           WeatherInfoClient weatherInfoClient) {
        this.geocodingClient = geocodingClient;
        this.weatherInfoClient = weatherInfoClient;
    }

    public MainPageResponse getWeatherInfo(MainPageRequest mainPageRequest) {

        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        Grid grid = GpsToGridConverter.convertToGrid(mainPageRequest.latitude(),
                mainPageRequest.longitude());

        WeatherApiResponse weatherApiResponse = weatherInfoClient.getWeatherInfo(grid, TimeUtil.roundDownTime());

        List<Item> itemList = weatherApiResponse.response().body().items().item();

        List<TodayWeather> todayWeathers = getTodayWeatherList(
                itemList);
        List<WeekWeather> weekWeathers = getWeekWeatherList(
                itemList);

        return new MainPageResponse(
                new Dashboard(addressDTO, weekWeathers.getFirst().maxTemp(), weekWeathers.getFirst().minTemp(), todayWeathers.getFirst().temp()),
                todayWeathers,
                weekWeathers);

    }

    private AddressDTO getAddressFromGPS(Double longitude, Double latitude) {
        ReverseGeocodingResponse response = geocodingClient
                .doReverseGeocoding(longitude, latitude);

        StructureData structure = response.response().result().getFirst().structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }

    private List<TodayWeather> getTodayWeatherList(List<WeatherApiResponse.Item> items) {

        Map<String, Map<String, String>> groupedByTime = items.stream()
                .filter(item -> item.fcstDate().equals(getCurrentDate()))
                .filter(item -> List.of("TMP", "SKY", "PTY").contains(item.category()))
                .collect(Collectors.groupingBy(
                        WeatherApiResponse.Item::fcstTime,
                        Collectors.toMap(WeatherApiResponse.Item::category, WeatherApiResponse.Item::fcstValue)
                ));

        return groupedByTime.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
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

    private List<WeekWeather> getWeekWeatherList(List<WeatherApiResponse.Item> items) {

        Map<String, Map<String, List<String>>> groupedByDate = items.stream()
                .filter(item -> List.of("TMN", "TMX", "SKY", "PTY").contains(item.category()))
                .collect(Collectors.groupingBy(
                        WeatherApiResponse.Item::fcstDate,
                        Collectors.groupingBy(
                                WeatherApiResponse.Item::category,
                                Collectors.mapping(WeatherApiResponse.Item::fcstValue, Collectors.toList())
                        )
                ));

        return groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new WeekWeather(
                        convertToDayOfWeek(entry.getKey()),
                        //TODO: 받아온 API 데이터에 최고기온, 최저기온 값이 없을 때 다음 페이지에 대한 API 재요청 로직이 필요함
                        (int) Math.round(Double.parseDouble(entry.getValue().getOrDefault("TMX", List.of("0")).stream().findFirst().orElse("0"))), // TMX 처리
                        (int) Math.round(Double.parseDouble(entry.getValue().getOrDefault("TMN", List.of("0")).stream().findFirst().orElse("0"))), // TMN 처리
                        getWeatherDescription(
                                getMostFrequent(entry.getValue().getOrDefault("SKY", List.of("1"))), // SKY 값 중 최빈값 선택
                                getMostFrequent(entry.getValue().getOrDefault("PTY", List.of("0")))  // PTY 값 중 최빈값 선택
                        )
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

    private String getMostFrequent(List<String> values) {
        return values.stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue)) // 가장 많이 나온 값 선택
                .map(Map.Entry::getKey)
                .orElse("0");
    }
}
