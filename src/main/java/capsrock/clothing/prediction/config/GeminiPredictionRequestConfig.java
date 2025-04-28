package capsrock.clothing.prediction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public record GeminiPredictionRequestConfig(
        String apiKey, String prompt, String baseUrl
) {

}