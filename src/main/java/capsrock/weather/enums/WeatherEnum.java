package capsrock.weather.enums;

public enum WeatherEnum {
    THUNDERSTORM(0, 200, 299),
    DRIZZLE(1, 300, 399),
    RAIN(2, 500, 505),
    SNOW(3, 600, 699),
    MIST(4, 701, 762),
    SQUALL(5, 771, 771),
    TORNADO(6, 781, 781),
    CLEAR(7, 800, 800),
    PARTLY_CLOUDY(8, 801, 802),
    CLOUDY(9, 803, 804),
    ICE_RAIN(10, 511, 599);


    private final Integer id;
    private final Integer minCode;
    private final Integer maxCode;
    

    WeatherEnum(Integer id, Integer minCode, Integer maxCode) {
        this.id = id;
        this.minCode = minCode;
        this.maxCode = maxCode;
    }


    public Integer getId() {
        return id;
    }

    public static WeatherEnum fromCode(Integer code) {
        for (WeatherEnum condition : values()) {
            if (code >= condition.minCode && code <= condition.maxCode) {
                return condition;
            }
        }
        return MIST; // 기본값 (혹시 모르는 7xx 코드 대응)
    }
}

