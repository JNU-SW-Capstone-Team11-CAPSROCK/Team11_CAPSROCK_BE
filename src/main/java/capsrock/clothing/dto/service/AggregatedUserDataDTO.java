// AggregatedUserDataDTO (새로운 DTO)
package capsrock.clothing.dto.service;

import capsrock.clothing.dto.client.request.ClothingData;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.member.dto.service.RecentLocationDTO;

import java.util.List;

public record AggregatedUserDataDTO(
        Long memberId,
        List<ClothingData> pastRecords, // ClothingRecordDTO 대신 바로 ClothingData 사용
        RecentLocationDTO location,
        FeelsLikeTemp todaysFeelsLikeTemp
) {
}