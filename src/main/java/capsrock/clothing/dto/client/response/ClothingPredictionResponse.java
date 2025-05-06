package capsrock.clothing.dto.client.response;

import java.util.List;

public record ClothingPredictionResponse(
        List<UserPrediction> userDataList
) {}

