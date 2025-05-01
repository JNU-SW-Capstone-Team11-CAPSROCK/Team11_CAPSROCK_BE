package capsrock.weather.dto.response;

import capsrock.weather.dto.service.Dashboard;
import capsrock.weather.dto.service.Next23HoursWeather;
import capsrock.weather.dto.service.NextFewDaysWeather;

import java.util.List;

public record WeatherResponse(
        Dashboard dashboard,
        List<Next23HoursWeather> next23HoursWeathers,
        List<NextFewDaysWeather> nextFewDaysWeathers
) {
}
