package capsrock.ultraviolet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ultraviolet")
public record UltravioletRequestConfig (
        String restApiKey,
        String baseRequestUrl
) {
}
