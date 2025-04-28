package capsrock.clothing.prediction.service;

import capsrock.clothing.prediction.client.ClothingGeminiClient;
import capsrock.mainPage.service.DailyWeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ClothingPredictService {


    private final DailyWeatherService dailyWeatherService;
    private final ClothingGeminiClient clothingGeminiClient;

    public ClothingPredictService(DailyWeatherService dailyWeatherService,
            ClothingGeminiClient clothingGeminiClient) {
        this.dailyWeatherService = dailyWeatherService;
        this.clothingGeminiClient = clothingGeminiClient;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void predict() throws JsonProcessingException {
        //gpt에게 다음날 보정치 예측 시키기
//        clothingGeminiClient.getPrediction();
    }

}
