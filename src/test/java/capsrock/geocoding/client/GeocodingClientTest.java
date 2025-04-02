package capsrock.geocoding.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GeocodingClientTest {
    @Autowired
    private GeocodingClient geocodingClient;

    @Test
    void doReverseGeocoding() {
        geocodingClient.doReverseGeocoding(126.9779692, 37.566535);
    }
}