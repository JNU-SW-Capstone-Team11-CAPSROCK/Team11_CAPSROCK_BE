package capsrock.fineDust.client;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FineDustClientTest {

    @Autowired
    private FineDustInfoClient fineDustInfoClient;

    @Test
    @DisplayName("미세먼지 정보")
    void getFineDustResponse() {
        System.out.println("fineDustInfoClient.getWeatherInfo(new Grid(60, 125)) = "
                + fineDustInfoClient.getFineDustResponse(35.0759421018392, 126.774759454703));
    }
}
