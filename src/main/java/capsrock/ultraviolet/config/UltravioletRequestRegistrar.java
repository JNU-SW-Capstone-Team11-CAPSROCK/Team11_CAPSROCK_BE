package capsrock.ultraviolet.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(UltravioletRequestConfig.class)
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-ultraviolet.properties"})
public class UltravioletRequestRegistrar {
}
