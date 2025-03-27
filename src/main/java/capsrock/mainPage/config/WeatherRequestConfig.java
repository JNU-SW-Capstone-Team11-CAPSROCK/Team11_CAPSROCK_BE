package capsrock.mainPage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherRequestConfig(
        String restApiKey,
        String requestUrl
) { }
