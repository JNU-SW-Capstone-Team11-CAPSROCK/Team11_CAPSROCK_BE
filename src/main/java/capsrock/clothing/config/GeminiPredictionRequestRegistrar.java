package capsrock.clothing.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(GeminiPredictionRequestConfig.class)
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-gemini.properties"})
public class GeminiPredictionRequestRegistrar {

}
