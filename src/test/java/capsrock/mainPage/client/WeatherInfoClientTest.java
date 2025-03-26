package capsrock.mainPage.client;

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
    void getWeatherInfo() {
        weatherInfoClient.getWeatherInfo();
    }
}
