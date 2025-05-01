package capsrock.clothing.prediction.dto.client.request;

import capsrock.clothing.prediction.model.vo.FeelsLikeTemp;
import java.util.List;

public record OneUserData(
        Long userId,
        List<ClothingData> clothingData,
        FeelsLikeTemp todayFeelsLikeTemp
) {

}
