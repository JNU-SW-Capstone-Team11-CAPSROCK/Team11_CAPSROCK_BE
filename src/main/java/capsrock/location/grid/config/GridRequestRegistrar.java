package capsrock.location.grid.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(GridRequestConfig.class)
@PropertySource({"classpath:application-weather.properties",
        "classpath:application-secret.properties"})
public class GridRequestRegistrar {

}
