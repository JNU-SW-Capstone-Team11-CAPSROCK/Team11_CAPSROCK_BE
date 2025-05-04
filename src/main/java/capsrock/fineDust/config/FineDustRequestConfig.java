package capsrock.fineDust.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fine-dust")
public record FineDustRequestConfig(
        String restApiKey,
        String baseRequestUrl,
        String airPollutionPath
) {
}
