package capsrock.fineDust.service;


import capsrock.fineDust.dto.request.FineDustRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FineDustServiceTest {

    @Autowired
    private FineDustService fineDustService;

    @Test
    void getFineDustResponseTest() {

        System.out.println(
                "hourlyWeatherService.getHourlyWeather(35.0759421018392, 126.774759454703) = "
                        + fineDustService.getFineDustResponse(new FineDustRequest(35.0759421018392, 126.774759454703)));

    }
}
