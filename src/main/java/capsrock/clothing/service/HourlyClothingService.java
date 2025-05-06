package capsrock.clothing.service;

import capsrock.clothing.dto.service.Next23HoursClothingDTO;
import capsrock.clothing.enums.ClothingType;
import capsrock.clothing.model.vo.Correction;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.HourlyWeatherResponse;
import capsrock.weather.dto.response.HourlyWeatherResponse.WeatherData;
import capsrock.weather.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HourlyClothingService {

    private static final Integer MORNING_HOUR = 6;
    private static final Integer NOON_HOUR = 12;
    private static final Integer EVENING_HOUR = 18;
    private static final Integer HOURS_IN_DAY = 24;

    private final WeatherInfoClient weatherInfoClient;

    public List<Next23HoursClothingDTO> getHourlyClothing(Double latitude, Double longitude,
            Correction correction) {

        HourlyWeatherResponse response = weatherInfoClient.getHourlyWeatherResponse(
                latitude, longitude);

        List<Next23HoursClothingDTO> clothingDTOList = new ArrayList<>();

        for (WeatherData data : response.list()) {
            Double feelsLike = data.main().feelsLike();
            String dateTime = TimeUtil.convertUnixTimeStamp(data.dt());
            Integer hour = TimeUtil.getHourFromDateTimeString(dateTime);
            Double correctionValue = calculateHourlyCorrection(correction, hour);
            Integer clothingId = ClothingType.fromTemperature(feelsLike + correctionValue).getId();

            clothingDTOList.add(new Next23HoursClothingDTO(
                    dateTime, clothingId, feelsLike, correctionValue, feelsLike + correctionValue
            ));
        }
        return clothingDTOList;
    }


    private Double calculateHourlyCorrection(Correction correction, Integer hour) {

        // 각 시간대별 보정값
        Double morningCorr = correction.morningCorrection();  // 6시 값
        Double noonCorr = correction.noonCorrection();        // 12시 값
        Double eveningCorr = correction.eveningCorrection();  // 18시 값

        // 선형 보간 계산
        if (hour >= 0 && hour < MORNING_HOUR) {
            double ratio = (double) hour / MORNING_HOUR;

            return eveningCorr + (morningCorr - eveningCorr) * ratio;
        } else if (hour >= MORNING_HOUR && hour < NOON_HOUR) {

            double ratio = (double) (hour - MORNING_HOUR) / (NOON_HOUR - MORNING_HOUR);
            return morningCorr + (noonCorr - morningCorr) * ratio;
        } else if (hour >= NOON_HOUR && hour < EVENING_HOUR) {

            double ratio = (double) (hour - NOON_HOUR) / (EVENING_HOUR - NOON_HOUR);
            return noonCorr + (eveningCorr - noonCorr) * ratio;
        } else {

            double ratio = (double) (hour - EVENING_HOUR) / (HOURS_IN_DAY - EVENING_HOUR);
            return eveningCorr + (morningCorr - eveningCorr) * ratio;
        }
    }

}
