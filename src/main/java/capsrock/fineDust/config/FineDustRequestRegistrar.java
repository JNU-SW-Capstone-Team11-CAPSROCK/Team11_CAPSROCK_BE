package capsrock.fineDust.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(FineDustRequestConfig.class)
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-fine-dust.properties"})
public class FineDustRequestRegistrar {
}
