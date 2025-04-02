package capsrock.mainPage.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HourlyWeatherServiceTest {

    @Autowired
    private HourlyWeatherService hourlyWeatherService;

    @Test
    void getHourlyWeather() {
        System.out.println(
                "hourlyWeatherService.getHourlyWeather(35.0759421018392, 126.774759454703) = "
                        + hourlyWeatherService.getHourlyWeather(35.0759421018392,
                        126.774759454703));

    }
}