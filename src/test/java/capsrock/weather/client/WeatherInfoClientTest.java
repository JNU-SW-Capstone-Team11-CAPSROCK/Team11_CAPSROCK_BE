package capsrock.weather.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherInfoClientTest {

    @Autowired
    private WeatherInfoClient weatherInfoClient;

    @Test
    @DisplayName("날씨 정보")
    void getHourlyWeatherResponse() {
        System.out.println("weatherInfoClient.getWeatherInfo(new Grid(60, 125)) = "
                + weatherInfoClient.getHourlyWeatherResponse(35.0759421018392, 126.774759454703));
    }

    @Test
    @DisplayName("현재 시간")
    void getTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        System.out.println("now = " + now);
    }

    @Test
    void getDailyWeather() {
        System.out.println(
                "weatherInfoClient.getDailyWeatherResponse(35.0759421018392, 126.774759454703) = "
                        + weatherInfoClient.getDailyWeatherResponse(35.0759421018392,
                        126.774759454703, 7));
    }
}
