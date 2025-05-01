package capsrock.weather.controller;

import capsrock.weather.dto.request.WeatherRequest;
import capsrock.weather.dto.response.WeatherResponse;
import capsrock.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(WeatherRequest weatherRequest){
        return new ResponseEntity<>(weatherService.getWeather(weatherRequest), HttpStatus.OK);
    }
}
