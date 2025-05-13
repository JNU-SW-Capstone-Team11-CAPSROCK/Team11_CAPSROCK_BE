package capsrock.ultraviolet.dto.response;

import capsrock.ultraviolet.dto.service.Dashboard;
import capsrock.ultraviolet.dto.service.Next23HoursUltravioletLevel;
import capsrock.ultraviolet.dto.service.NextFewDaysUltravioletLevel;

import java.util.List;

public record UltravioletResponse(
        Dashboard dashboard,
        List<Next23HoursUltravioletLevel> next23HoursUltravioletLevels,
        List<NextFewDaysUltravioletLevel> nextFewDaysUltravioletLevels
) {
}
