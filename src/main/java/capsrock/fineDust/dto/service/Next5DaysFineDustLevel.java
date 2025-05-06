package capsrock.fineDust.dto.service;

import java.util.List;

public record Next5DaysFineDustLevel(
        String day,
        String dayOfWeek,
        List<Integer> dailyFineDustLevel
) {
}
