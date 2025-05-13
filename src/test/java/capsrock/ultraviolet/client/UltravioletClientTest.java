package capsrock.ultraviolet.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UltravioletClientTest {

    @Autowired
    private UltravioletInfoClient ultravioletInfoClient;

    @Test
    @DisplayName("자외선 정보")
    void getUltravioletApiResponseTest() {
        System.out.println("fineDustInfoClient.getWeatherInfo(new Grid(60, 125)) = "
                + ultravioletInfoClient.getUltravioletResponse(35.0759421018392, 126.774759454703));
    }
}
