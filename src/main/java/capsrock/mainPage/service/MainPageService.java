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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MainPageService {

    private final GeocodingClient geocodingClient;
    private final WeatherInfoClient weatherInfoClient;
    private final ObjectMapper objectMapper;

    public MainPageService(GeocodingClient geocodingClient,
            WeatherInfoClient weatherInfoClient,
            ObjectMapper objectMapper) {
        this.geocodingClient = geocodingClient;
        this.weatherInfoClient = weatherInfoClient;
        this.objectMapper = objectMapper;
    }

    public MainPageResponse getWeatherInfo(MainPageRequest mainPageRequest) {
        AddressDTO addressDTO = getAddressFromGPS(mainPageRequest.longitude(),
                mainPageRequest.latitude());

        String weatherInfo = "";

        List<TodayWeather> todayWeathers = parseTodayWeatherInfo(weatherInfo);
        List<WeekWeather> weekWeathers = parseWeekWeatherData(weatherInfo);

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

    private List<TodayWeather> parseTodayWeatherInfo(String rawData) {
        try {

            JsonNode rootNode = objectMapper.readTree(rawData);

            // 기상청 API 응답 구조에 맞게 데이터 추출
            JsonNode itemsNode = rootNode
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            List<TodayWeather> todayWeathers = new ArrayList<>();

            String today = getCurrentDate();

            // 응답 데이터를 TodayWeather 객체로 변환
            if (itemsNode.isArray()) {
                String currentTime = "";
                String weather = "";
                int temp = 0;

                for (JsonNode item : itemsNode) {
                    // 오늘 날짜의 데이터만 처리
                    String fcstDate = item.path("fcstDate").asText();
                    if (!fcstDate.equals(today)) {
                        continue;
                    }

                    String fcstTime = item.path("fcstTime").asText();
                    String category = item.path("category").asText();
                    String value = item.path("fcstValue").asText();

                    // 시간이 바뀌면 새 객체 생성
                    if (!currentTime.equals(fcstTime) && !currentTime.isEmpty()) {
                        todayWeathers.add(new TodayWeather(currentTime, weather, temp));
                        weather = ""; // 초기화
                    }

                    currentTime = fcstTime;

                    // 카테고리별 데이터 처리
                    switch (category) {
                        case "SKY": // 하늘 상태(맑음, 구름많음, 흐림)
                            weather = interpretSkyValue(value);
                            break;
                        case "PTY": // 강수 형태(비, 비/눈, 눈, 소나기)
                            if (!value.equals("0")) { // 0이 아니면 강수 있음
                                weather = interpretPtyValue(value);
                            }
                            break;
                        case "T1H": // 기온
                        case "TMP": // 기온 (API 버전에 따라 다를 수 있음)
                            temp = Integer.parseInt(value);
                            break;
                    }
                }

                // 마지막 시간대 데이터 추가
                if (!currentTime.isEmpty()) {
                    todayWeathers.add(new TodayWeather(currentTime, weather, temp));
                }
            }

            return todayWeathers;

        } catch (Exception e) {
            return List.of(); // 에러 시 빈 리스트 반환
        }

    }

    private String interpretSkyValue(String skyValue) {
        return switch (skyValue) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "알 수 없음";
        };
    }

    private String interpretPtyValue(String ptyValue) {
        return switch (ptyValue) {
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            default -> "알 수 없음";
        };
    }

    private List<WeekWeather> parseWeekWeatherData(String rawData) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawData);
            JsonNode itemsNode = rootNode
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            Map<String, WeekWeather> weekWeatherMap = new HashMap<>();
            String today = getCurrentDate();

            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String fcstDate = item.path("fcstDate").asText();
                    if (fcstDate.compareTo(today) < 0) {
                        continue; // 오늘 이전 데이터 제외
                    }

                    // Map에 날짜별 WeekWeather 객체가 없으면 새로 생성
                    weekWeatherMap.putIfAbsent(fcstDate, new WeekWeather(
                            convertToDayOfWeek(fcstDate), Integer.MIN_VALUE, Integer.MAX_VALUE, "맑음"
                    ));

                    WeekWeather weatherData = weekWeatherMap.get(fcstDate);
                    String category = item.path("category").asText();
                    String value = item.path("fcstValue").asText();

                    switch (category) {
                        case "TMAX": // 최고 기온
                            weekWeatherMap.put(fcstDate, new WeekWeather(
                                    weatherData.dayOfWeek(),
                                    Math.max(weatherData.maxTemp(), Integer.parseInt(value)),
                                    weatherData.minTemp(),
                                    weatherData.weather()
                            ));
                            break;
                        case "TMIN": // 최저 기온
                            weekWeatherMap.put(fcstDate, new WeekWeather(
                                    weatherData.dayOfWeek(),
                                    weatherData.maxTemp(),
                                    Math.min(weatherData.minTemp(), Integer.parseInt(value)),
                                    weatherData.weather()
                            ));
                            break;
                        case "SKY":
                            weekWeatherMap.put(fcstDate, new WeekWeather(
                                    weatherData.dayOfWeek(),
                                    weatherData.maxTemp(),
                                    weatherData.minTemp(),
                                    interpretSkyValue(value)
                            ));
                            break;
                        case "PTY":
                            if (!value.equals("0")) {
                                weekWeatherMap.put(fcstDate, new WeekWeather(
                                        weatherData.dayOfWeek(),
                                        weatherData.maxTemp(),
                                        weatherData.minTemp(),
                                        interpretPtyValue(value)
                                ));
                            }
                            break;
                    }
                }
            }

            return new ArrayList<>(weekWeatherMap.values());

        } catch (Exception e) {
            return List.of();
        }
    }

    private String getCurrentDate() {
        return LocalDate.now().toString().replace("-", "");
    }

    private String convertToDayOfWeek(String fcstDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(fcstDate, formatter);

        // LocalDate 객체에서 요일을 구하고, 요일 이름을 반환
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.KOREAN);
    }

}
