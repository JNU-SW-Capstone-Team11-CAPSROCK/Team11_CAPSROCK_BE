package capsrock.clothing.enums;

import java.util.Objects;
import lombok.Getter;

@Getter
public enum ClothingType {
    // ID, 설명, 최저 온도, 최고 온도, 추천 상의, 추천 하의
    VERY_HOT(1, 28, Integer.MAX_VALUE),
    HOT(2, 23, 27),
    WARM(3, 20, 22),
    MILD(4, 17, 19),
    COOL(5, 12, 16),
    COLD(6, 9, 11),
    VERY_COLD(7, 5, 8),
    FREEZING(8, Integer.MIN_VALUE, 4);

    private final Integer id;
    private final Integer minTemp;
    private final Integer maxTemp;

    ClothingType(Integer id, Integer minTemp, Integer maxTemp) {
        this.id = id;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public static ClothingType fromTemperature(Double temperature) {
        int roundedTemp = (int) Math.round(temperature);

        for (ClothingType type : values()) {
            if (roundedTemp >= type.minTemp && roundedTemp <= type.maxTemp) {
                return type;
            }
        }
        return FREEZING;
    }

    public static ClothingType fromId(Integer id) {
        for (ClothingType type : values()) {
            if (Objects.equals(type.id, id)) {
                return type;
            }
        }
        return null;
    }
}