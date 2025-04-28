package capsrock.clothing.prediction.dto.response;

import java.util.List;

public record ClothingPredictionResponse(
        List<UserPrediction> oneUserData
) {}

