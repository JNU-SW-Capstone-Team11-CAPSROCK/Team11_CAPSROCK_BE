package capsrock.mainPage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DailyWeatherServiceTest {

    @Autowired
    private DailyWeatherService dailyWeatherService;



    @Test
    void getNextFewDaysWeather() {
        System.out.println(
                "dailyWeatherService.getNext7DaysWeather(35.0759421018392, 126.774759454703) = "
                        + dailyWeatherService.getNextFewDaysWeather(35.0759421018392,
                        126.774759454703, 7));
    }
}