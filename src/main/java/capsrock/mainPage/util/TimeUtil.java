package capsrock.mainPage.util;

import capsrock.mainPage.dto.TimeDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final String[] API_UPDATE_TIME = {
            "0220", "0520", "0820", "1120", "1420", "1720", "2020", "2320"
    };

    private static final String[] BASE_TIME = {
            "0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"
    };

    public static TimeDTO roundDownTime() {
        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HHmm"));

        for (int i = API_UPDATE_TIME.length - 1; i >= 0; i--) {
            if (currentTime.compareTo(API_UPDATE_TIME[i]) >= 0) {
                return new TimeDTO(yyyyMMdd, BASE_TIME[i]);
            }
        }
        // 첫 번째 업데이트 시간 이전이면 전날 23:00 기준 반환
        return new TimeDTO((now.minusDays(1)).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "2300");
    }

    public static TimeDTO plus23Hours(TimeDTO timeDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime dateTime = LocalDateTime
                .parse(timeDTO.yyyyMMdd() + timeDTO.hhMM(), formatter)
                .plusHours(23);

        return new TimeDTO(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                dateTime.format(DateTimeFormatter.ofPattern("HHmm")));
    }

    public static String getHH00() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH")) + "00";
    }

    public static String getHH00(String HHmm) {
        return HHmm.substring(0, 2) + "00";
    }

    public static boolean isIn23Hours(String fcstDate, String fcstTime, TimeDTO timeDTO) {
        TimeDTO next23Hours = TimeUtil.plus23Hours(timeDTO);
        return (fcstDate.equals(timeDTO.yyyyMMdd()) && fcstTime.compareTo(TimeUtil.getHH00()) >= 0) ||
                (fcstDate.equals(next23Hours.yyyyMMdd()) && fcstTime.compareTo(TimeUtil.getHH00(next23Hours.hhMM())) <= 0);
    }
}

