package capsrock.clothing.service;

import capsrock.clothing.dto.service.NextFewDaysClothingDTO;
import capsrock.clothing.enums.ClothingType;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.dto.response.DailyWeatherResponse.FeelsLike;
import capsrock.weather.dto.response.DailyWeatherResponse.Forecast;
import capsrock.weather.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyClothingService {

    private final WeatherInfoClient weatherInfoClient;

    public List<NextFewDaysClothingDTO> getNextFewDaysClothing(Double latitude, Double longitude,
            Integer days) {
        DailyWeatherResponse response = weatherInfoClient.getDailyWeatherResponse(latitude,
                longitude, days);

        List<NextFewDaysClothingDTO> nextFewDaysClothingDTOList = new ArrayList<>();

        for (Forecast forecast : response.list()) {
            FeelsLike feelsLike = forecast.feelsLike();
            Double maxFeelsLike = Math.max(feelsLike.morn(),
                    Math.max(feelsLike.day(), feelsLike.eve()));
            Double minFeelsLike = Math.min(feelsLike.morn(),
                    Math.min(feelsLike.day(), feelsLike.eve()));
            Integer maxClothing = ClothingType.fromTemperature(maxFeelsLike).getId();
            Integer minClothing = ClothingType.fromTemperature(minFeelsLike).getId();
            String dateTime = TimeUtil.convertUnixTimeStamp(forecast.dt());
            String dayOfWeek = TimeUtil.getDayOfWeek(dateTime);

            nextFewDaysClothingDTOList.add(new NextFewDaysClothingDTO(
                    dateTime, dayOfWeek, maxFeelsLike, minFeelsLike, maxClothing, minClothing
            ));
        }

        return nextFewDaysClothingDTOList;
    }

}
