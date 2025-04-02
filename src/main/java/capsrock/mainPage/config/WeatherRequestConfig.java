package capsrock.mainPage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherRequestConfig(
        String restApiKey,
        String baseRequestUrl,
        String currentWeatherPath,
        String forecastPath,
        String hourlyPath,
        String dailyPath
) { }
