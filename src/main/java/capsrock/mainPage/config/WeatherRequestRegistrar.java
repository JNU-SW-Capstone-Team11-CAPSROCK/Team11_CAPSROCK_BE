package capsrock.mainPage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(WeatherRequestConfig.class)  // WeatherRequestConfig을 빈으로 등록
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-grid.properties"})
public class WeatherRequestRegistrar {

}
