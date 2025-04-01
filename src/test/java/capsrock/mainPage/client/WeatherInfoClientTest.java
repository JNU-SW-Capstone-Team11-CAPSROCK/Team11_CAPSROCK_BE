package capsrock.mainPage.client;

import capsrock.mainPage.util.TimeUtil;

import capsrock.location.grid.dto.Grid;
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
    void getWeatherInfo() {
        System.out.println("weatherInfoClient.getWeatherInfo(new Grid(60, 125)) = "
                + weatherInfoClient.getWeatherInfo(new Grid(60, 125), TimeUtil.roundDownTime()));
    }

    @Test
    @DisplayName("현재 시간")
    void getTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        System.out.println("now = " + now);
    }
}
