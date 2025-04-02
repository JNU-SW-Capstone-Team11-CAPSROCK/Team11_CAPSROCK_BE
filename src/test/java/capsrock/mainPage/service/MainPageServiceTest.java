package capsrock.mainPage.service;

import capsrock.mainPage.dto.request.MainPageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainPageServiceTest {

    @Autowired
    private MainPageService mainPageService;


    @Test
    void getWeatherInfo() {
        var mainpageResponse = mainPageService.getWeatherInfo(new MainPageRequest(35.0759421018392, 126.774759454703));
        System.out.println("mainpageResponse = " + mainpageResponse);
    }
}