package capsrock.clothing.prediction.dto.response;

public record UserPrediction(
        Long userId,
        CorrectionValues predictedCorrectionValues
) {}
