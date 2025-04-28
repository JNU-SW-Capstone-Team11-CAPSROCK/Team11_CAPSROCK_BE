package capsrock.clothing.prediction.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserPrediction(
        Long userId,
        CorrectionValues predictedCorrectionValues
) {}
