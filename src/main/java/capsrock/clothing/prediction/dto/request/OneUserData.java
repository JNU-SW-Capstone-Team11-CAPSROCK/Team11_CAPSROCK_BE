package capsrock.clothing.prediction.dto.request;

import java.util.List;

public record OneUserData(
        Long userId,
        List<ClothingData> clothingData
) {

}
