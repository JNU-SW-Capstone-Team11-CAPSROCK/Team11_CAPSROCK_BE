package capsrock.clothing.prediction.client;


import capsrock.clothing.prediction.config.GeminiPredictionRequestConfig;
import capsrock.clothing.prediction.dto.client.request.ClothingPredictionRequest;
import capsrock.clothing.prediction.dto.client.response.ClothingPredictionResponse;
import capsrock.clothing.prediction.enums.GeminiModel;
import capsrock.clothing.prediction.factory.ClothingPredictionResponseSchemaFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import com.google.genai.Client;

@Component
public class ClothingGeminiClient {

    //async
    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final GeminiPredictionRequestConfig geminiPredictionRequestConfig;
    private final ClothingPredictionResponseSchemaFactory clothingPredictionResponseSchemaFactory;

    ClothingGeminiClient(GeminiPredictionRequestConfig geminiPredictionRequestConfig,
            ClothingPredictionResponseSchemaFactory clothingPredictionResponseSchemaFactory) {

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.geminiPredictionRequestConfig = geminiPredictionRequestConfig;
        this.clothingPredictionResponseSchemaFactory = clothingPredictionResponseSchemaFactory;
        this.geminiClient = Client
                .builder()
                .apiKey(this.geminiPredictionRequestConfig.apiKey())
                .build();
    }

    public CompletableFuture<ClothingPredictionResponse> getPrediction(
            ClothingPredictionRequest requestDTO) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(requestDTO);

        List<Content> contents = Arrays.asList(
                getUserPart(geminiPredictionRequestConfig.prompt()),
                getUserPart(jsonString)
        );

        GenerateContentConfig config = getConfig(
                clothingPredictionResponseSchemaFactory.getClothingPredictionResponseSchema()
        );

        return geminiClient.async.models.generateContent(
                        GeminiModel.PRO_2_5_PRO_EXP_03_25.getModelName(),
                        contents,
                        config
                )
                .thenApply(response -> {
                    try {
                        return objectMapper.readValue(response.text(),
                                ClothingPredictionResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    private GenerateContentConfig getConfig(Schema schema) {
        return GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .candidateCount(1)
                .responseSchema(schema)
                .build();
    }

    private Content getUserPart(String userContent) {
        Part userPart = Part.builder().text(userContent).build();
        return Content.builder().role("user").parts(Arrays.asList(userPart)).build();
    }
}


