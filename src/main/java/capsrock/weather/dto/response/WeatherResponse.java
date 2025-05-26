package capsrock.weather.dto.response;

import capsrock.weather.dto.service.Dashboard;
import capsrock.weather.dto.service.NextFewHoursWeather;
import capsrock.weather.dto.service.NextFewDaysWeather;

import java.util.List;

public record WeatherResponse(
        Dashboard dashboard,
        List<NextFewHoursWeather> nextFewHoursWeathers,
        List<NextFewDaysWeather> nextFewDaysWeathers
) {
}
