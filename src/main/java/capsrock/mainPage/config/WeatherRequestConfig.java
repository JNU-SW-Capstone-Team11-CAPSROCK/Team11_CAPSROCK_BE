package capsrock.mainPage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherRequestConfig(
//        String yyyymmddttmm,
//        String stn,
        String restApiKey,
        String requestUrl
) { }
