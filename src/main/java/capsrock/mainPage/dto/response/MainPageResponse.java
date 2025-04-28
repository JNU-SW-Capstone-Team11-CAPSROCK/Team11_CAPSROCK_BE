package capsrock.mainPage.dto.response;

import capsrock.mainPage.dto.service.Dashboard;
import capsrock.mainPage.dto.service.Next23HoursWeather;
import capsrock.mainPage.dto.service.NextFewDaysWeather;

import java.util.List;

public record MainPageResponse(
        Dashboard dashboard,
        List<Next23HoursWeather> next23HoursWeathers,
        List<NextFewDaysWeather> nextFewDaysWeathers
) {
}
