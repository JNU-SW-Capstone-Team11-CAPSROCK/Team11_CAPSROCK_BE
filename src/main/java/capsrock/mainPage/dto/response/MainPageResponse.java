package capsrock.mainPage.dto.response;

import capsrock.mainPage.dto.Dashboard;
import capsrock.mainPage.dto.TodayWeather;
import capsrock.mainPage.dto.WeekWeather;

import java.util.List;

public record MainPageResponse(
        Dashboard dashboard,
        List<TodayWeather> today,
        List<WeekWeather> week
) {
}
