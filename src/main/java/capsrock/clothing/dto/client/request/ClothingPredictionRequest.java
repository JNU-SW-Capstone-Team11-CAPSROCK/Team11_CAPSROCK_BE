package capsrock.clothing.dto.client.request;

import java.util.List;

public record ClothingPredictionRequest(
        List<OneUserData> oneUserData
) { }

