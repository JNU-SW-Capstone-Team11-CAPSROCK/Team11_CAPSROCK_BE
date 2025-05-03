package capsrock.clothing.factory;

import com.google.common.collect.ImmutableMap;
import com.google.genai.types.Schema;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClothingPredictionResponseSchemaFactory {

    private Schema clothingPredictionResponseSchema() {
        // 가장 안쪽부터 스키마 구성
        Schema morningSchema = Schema.builder()
                .type("number")
                .description("Correction value for morning")
                .build();

        Schema noonSchema = Schema.builder()
                .type("number")
                .description("Correction value for noon")
                .build();

        Schema eveningSchema = Schema.builder()
                .type("number")
                .description("Correction value for evening")
                .build();

        // predictedCorrectionValues 객체 스키마 구성
        Map<String, Schema> predictedCorrectionProperties = ImmutableMap.of(
                "morningCorrection", morningSchema,
                "noonCorrection", noonSchema,
                "eveningCorrection", eveningSchema
        );
        List<String> predictedCorrectionRequired = Arrays.asList("morningCorrection",
                "noonCorrection", "eveningCorrection");

        Schema predictedCorrectionValuesSchema = Schema.builder()
                .type("object") // JSON 타입: object
                .description("Predicted correction values for morning, noon, and evening")
                .properties(predictedCorrectionProperties) // properties 맵 설정
                .required(predictedCorrectionRequired) // required 리스트 설정
                .build();

        // userId 스키마 구성
        Schema userIdSchema = Schema.builder()
                .type("integer") // JSON 타입: integer
                .description("The ID of the user")
                .build();

        // userDataList 배열의 items (개별 사용자 객체) 스키마 구성
        Map<String, Schema> itemProperties = ImmutableMap.of(
                "userId", userIdSchema,
                "predictedCorrectionValues", predictedCorrectionValuesSchema
        );
        List<String> itemRequired = Arrays.asList("userId", "predictedCorrectionValues");

        Schema oneUserDataItemSchema = Schema.builder()
                .type("object") // JSON 타입: object
                // Note: items 스키마 자체에는 보통 description이 없습니다.
                .properties(itemProperties) // properties 맵 설정
                .required(itemRequired) // required 리스트 설정
                .build();

        // userDataList 배열 스키마 구성
        List<String> oneUserDataRequired = Arrays.asList("userDataList");

        Schema oneUserDataSchema = Schema.builder()
                .type("array") // <-- 타입은 String으로 지정합니다
                .description("List of predicted correction values for each user") // description
                .items(oneUserDataItemSchema) // items 스키마 설정 (배열 요소의 스키마 연결)
                .build();

        Map<String, Schema> parametersProperties = ImmutableMap.of(
                "userDataList", oneUserDataSchema // 'userDataList' 스키마 추가
        );
        List<String> parametersRequired = Arrays.asList("userDataList");

        return Schema.builder()
                .type("object")
                .description("Response schema for clothing prediction API")
                .properties(parametersProperties) // properties 맵 설정
                .required(parametersRequired) // required 리스트 설정
                .build();
    }

    public Schema getClothingPredictionResponseSchema() {
        return clothingPredictionResponseSchema();
    }
}
