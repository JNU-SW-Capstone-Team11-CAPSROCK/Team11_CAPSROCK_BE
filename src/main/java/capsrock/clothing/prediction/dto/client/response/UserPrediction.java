package capsrock.clothing.prediction.dto.client.response;

import capsrock.clothing.prediction.model.vo.Correction;

public record UserPrediction(
        Long userId,
        Correction predictedCorrectionValues
) {}
