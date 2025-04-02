package capsrock.mainPage.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static String convertUnixTimeStamp(String utcTime) {
        //todo : 나중에 전세계 지원되게 리팩터링 하기

        // 입력된 UTC 시간 문자열을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(utcTime, formatter);

        // UTC 기준의 ZonedDateTime 생성
        ZonedDateTime utcDateTime = localDateTime.atZone(ZoneId.of("UTC"));

        // 한국 시간(KST, UTC+9)으로 변환
        ZonedDateTime kstDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        // 변환된 시간 문자열 반환
        return kstDateTime.format(formatter);
    }
}

