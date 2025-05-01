package capsrock.clothing.prediction.dto.client.request;

import java.util.List;

public record ClothingPredictionRequest(
        List<OneUserData> oneUserData
) { }

