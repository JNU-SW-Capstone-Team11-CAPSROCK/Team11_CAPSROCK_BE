package capsrock.clothing.dto.response;

import capsrock.clothing.dto.service.ClothingDashboard;
import capsrock.clothing.dto.service.Next23HoursClothingDTO;
import capsrock.clothing.dto.service.NextFewDaysClothingDTO;
import java.util.List;

public record ClothingResponse(
        Boolean havePendingFeedback,
        ClothingDashboard dashboard,
        List<Next23HoursClothingDTO> next23HoursClothing,
        List<NextFewDaysClothingDTO> nextFewDaysClothing
) {

}
