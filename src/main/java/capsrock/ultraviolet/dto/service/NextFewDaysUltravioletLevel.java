package capsrock.ultraviolet.dto.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record NextFewDaysUltravioletLevel(
        String day,
        String dayOfWeek,
        Map<String, Double> ultravioletLevels
) {
}
