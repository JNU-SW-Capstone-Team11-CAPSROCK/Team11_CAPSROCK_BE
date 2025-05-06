package capsrock.weather.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
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

    public static String convertUnixTimeStamp(Long unixTime) {
        // UTC 기준의 ZonedDateTime 생성
        ZonedDateTime utcDateTime = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("UTC"));

        // 한국 시간(KST, UTC+9)으로 변환
        ZonedDateTime kstDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        // 변환된 시간 문자열 반환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return kstDateTime.format(formatter);
    }


    public static String getDayOfWeek(String timeStamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate date = LocalDate.parse(timeStamp, formatter);

        // LocalDate 객체에서 요일을 구하고, 요일 이름을 반환
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.KOREAN);
    }

    public static Integer getHourFromDateTimeString(String dateTimeString) {
        // 입력 문자열의 형식 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 문자열을 LocalDateTime 객체로 파싱
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // 시간(hour)만 추출 (0-23)
        return dateTime.getHour();

    }

}

