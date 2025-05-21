package capsrock.ultraviolet.service;

import capsrock.ultraviolet.dto.request.UltravioletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UltravioletServiceTest {

    @Autowired
    private UltravioletService ultravioletService;

    @Test
    void getUltravioletResponseTest() {

        System.out.println(
                "ultravioletService.getUltravioletResponse(35.0759421018392, 126.774759454703) = "
                        + ultravioletService.getUltravioletResponse(new UltravioletRequest(35.0759421018392, 126.774759454703)));

    }
}
