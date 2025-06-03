package capsrock.fineDust.util;

public class AirQualityLevelConverter {

    public static int convertPm25ToLevel(double pm25) {
        if (pm25 <= 10) return 1; // Good (좋음)
        if (pm25 <= 25) return 2; // Fair (보통)
        if (pm25 <= 50) return 3; // Moderate (민감군주의)
        if (pm25 <= 75) return 4; // Poor (나쁨)
        return 5; // Very Poor (매우 나쁨) (> 75)
    }

    public static int convertPm10ToLevel(double pm10) {
        if (pm10 <= 20) return 1; // Good (좋음)
        if (pm10 <= 50) return 2; // Fair (보통)
        if (pm10 <= 100) return 3; // Moderate (민감군주의)
        if (pm10 <= 200) return 4; // Poor (나쁨)
        return 5; // Very Poor (매우 나쁨) (> 200)
    }

    public static int getAirQualityLevel(double pm25, double pm10) {
        return Math.max(convertPm25ToLevel(pm25), convertPm10ToLevel(pm10));
    }
}