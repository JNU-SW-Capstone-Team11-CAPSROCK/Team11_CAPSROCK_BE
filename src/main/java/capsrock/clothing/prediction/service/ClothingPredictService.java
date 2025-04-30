package capsrock.clothing.prediction.service;

import capsrock.clothing.prediction.client.ClothingGeminiClient;
import capsrock.weather.service.DailyWeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothingPredictService {

    private final DailyWeatherService dailyWeatherService;
    private final ClothingGeminiClient clothingGeminiClient;

    @Scheduled(cron = "0 0 1 * * ?")
    public void predict() throws JsonProcessingException {
        //gemini에게 다음날 보정치 예측 시키기
//        clothingGeminiClient.getPrediction();
    }

}
