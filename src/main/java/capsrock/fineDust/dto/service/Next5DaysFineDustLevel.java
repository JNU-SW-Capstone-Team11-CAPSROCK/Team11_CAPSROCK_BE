package capsrock.fineDust.dto.service;

import java.util.List;
import java.util.Map;

public record Next5DaysFineDustLevel(
        String day,
        String dayOfWeek,
        Map<String, Integer> dailyFineDustLevel
) {
}
