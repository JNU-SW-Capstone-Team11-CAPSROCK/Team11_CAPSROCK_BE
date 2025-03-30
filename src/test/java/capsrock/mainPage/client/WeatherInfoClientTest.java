package capsrock.mainPage.client;

import java.io.IOException;
import java.net.URI;

import capsrock.mainPage.dto.Grid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherInfoClientTest {

    @Autowired
    private WeatherInfoClient weatherInfoClient;

    private final RestClient restClient = RestClient.builder().build();


    @Test
    @DisplayName("날씨 정보")
    void getWeatherInfo() {
        weatherInfoClient.getWeatherInfo(new Grid(60, 125));
    }

    @Test
    @DisplayName("지점 정보")
    void getPoint() throws IOException {
        var response = restClient.get()
                .uri(URI.create("https://apihub.kma.go.kr/api/typ01/url/stn_inf.php?inf=SFC&stn=&tm=202211300900&authKey=xGeRQ48BRVankUOPAYVWJw"))
                .retrieve()
                .toEntity(String.class);

        System.out.println("response = " + response);
    }
}
