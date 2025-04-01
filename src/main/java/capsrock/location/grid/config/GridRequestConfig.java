package capsrock.location.grid.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "grid")
public record GridRequestConfig(
        String restApiKey,
        String requestUrl
) {
}
