package capsrock.clothing.dto.client.request;

import capsrock.clothing.model.vo.FeelsLikeTemp;
import java.util.List;

public record OneUserData(
        Long userId,
        List<ClothingData> clothingData,
        FeelsLikeTemp todayFeelsLikeTemp
) {

}
