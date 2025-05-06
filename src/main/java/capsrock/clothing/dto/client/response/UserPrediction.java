package capsrock.clothing.dto.client.response;

import capsrock.clothing.model.vo.Correction;

public record UserPrediction(
        Long userId,
        Correction predictedCorrectionValues
) {}
