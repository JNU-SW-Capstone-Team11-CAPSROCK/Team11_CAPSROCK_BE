package capsrock.fineDust.dto.response;

import capsrock.fineDust.dto.service.Dashboard;
import capsrock.fineDust.dto.service.Next23HoursFineDustLevel;
import capsrock.fineDust.dto.service.Next5DaysFineDustLevel;

import java.util.List;

public record FineDustResponse(
        Dashboard dashboard,
        List<Next23HoursFineDustLevel> next23HoursFineDustLevels,
        List<Next5DaysFineDustLevel> next5DaysFineDustLevels
) {
}
