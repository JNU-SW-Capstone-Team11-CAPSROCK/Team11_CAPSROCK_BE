package capsrock.ultraviolet.dto.service;

import java.util.List;

public record NextFewDaysUltravioletLevel(
        String day,
        String dayOfWeek,
        List<Integer> ultravioletLevels
) {
}
