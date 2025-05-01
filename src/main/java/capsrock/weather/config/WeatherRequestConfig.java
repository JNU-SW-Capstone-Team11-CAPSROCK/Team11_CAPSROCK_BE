package capsrock.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherRequestConfig(
        String restApiKey,
        String baseRequestUrl,
        String historyBaseRequestUrl,
        String currentWeatherPath,
        String forecastPath,
        String hourlyPath,
        String dailyPath
) { }
