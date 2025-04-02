package capsrock.mainPage.dto.response;

import capsrock.mainPage.dto.Dashboard;
import capsrock.mainPage.dto.Next23HoursWeather;
import capsrock.mainPage.dto.WeekWeather;

import java.util.List;

public record MainPageResponse(
        Dashboard dashboard,
        List<Next23HoursWeather> today,
        List<WeekWeather> week
) {
}
