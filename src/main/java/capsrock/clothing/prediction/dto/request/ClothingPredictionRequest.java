package capsrock.clothing.prediction.dto.request;

import java.util.List;

public record ClothingPredictionRequest(
        List<OneUserData> oneUserData,
        String comment
) { }

