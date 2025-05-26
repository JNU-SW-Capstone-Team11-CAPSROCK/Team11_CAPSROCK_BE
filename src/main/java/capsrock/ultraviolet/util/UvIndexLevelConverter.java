package capsrock.ultraviolet.util;

public class UvIndexLevelConverter {
    public static int convertUvIndexToLevel(double uvIndex) {
        if (uvIndex <= 2) return 1; // Low (낮음)
        if (uvIndex <= 5) return 2; // Moderate (보통)
        if (uvIndex <= 7) return 3; // High (높음)
        if (uvIndex <= 10) return 4; // Very High (매우 높음)
        return 5; // Extreme (극도로 높음)
    }
}
