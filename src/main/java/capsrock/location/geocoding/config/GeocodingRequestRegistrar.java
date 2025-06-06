package capsrock.location.geocoding.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(GeocodingRequestConfig.class)  // GeocodingRequestConfig을 빈으로 등록
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-geocoding.properties"})
public class GeocodingRequestRegistrar {

}
