package capsrock.clothing.prediction.client;

import capsrock.clothing.prediction.dto.request.ClothingData;
import capsrock.clothing.prediction.dto.request.ClothingPredictionRequest;
import capsrock.clothing.prediction.dto.request.MorningNoonEvening;
import capsrock.clothing.prediction.dto.request.OneUserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClothingGeminiClientTest {

    @Autowired
    private ClothingGeminiClient client;

    @Test
    void getPrediction() throws JsonProcessingException {

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        LocalDate twoDaysAgo = now.minusDays(2);
        LocalDate threeDaysAgo = now.minusDays(3);
        LocalDate fourDaysAgo = now.minusDays(4);
        LocalDate fiveDaysAgo = now.minusDays(5);
        LocalDate sixDaysAgo = now.minusDays(6);

        MorningNoonEvening sixDaysAgoFeels = new MorningNoonEvening(12.0, 19.4, 13.2);
        MorningNoonEvening fiveDaysAgoFeels = new MorningNoonEvening(10.0, 16.1, 12.2);
        MorningNoonEvening fourDaysAgoFeels = new MorningNoonEvening(13.0, 15.4, 10.2);
        MorningNoonEvening threeDaysAgoFeels = new MorningNoonEvening(9.4, 13.4, 3.2);
        MorningNoonEvening twoDaysAgoFeels = new MorningNoonEvening(5.1, 12.4, 7.8);
        MorningNoonEvening yesterdayFeels = new MorningNoonEvening(8.6, 21.4, 11.2);

        MorningNoonEvening sixDaysAgoCorrections = new MorningNoonEvening(3.1, 2.5, 1.6);
        MorningNoonEvening fiveDaysAgoCorrections = new MorningNoonEvening(-1.9, -0.2, 1.5);
        MorningNoonEvening fourDaysAgoCorrections = new MorningNoonEvening(0.1, 0.3, -0.1);
        MorningNoonEvening threeDaysAgoCorrections = new MorningNoonEvening(2.0, 1.4, 0.4);
        MorningNoonEvening twoDaysAgoCorrections = new MorningNoonEvening(0.3, -0.5, 1.0);
        MorningNoonEvening yesterdayCorrections = new MorningNoonEvening(0.4, 0.3, 0.4);

        MorningNoonEvening sixDaysAgoScores = new MorningNoonEvening(0.0, -1.0, -1.0);
        MorningNoonEvening fiveDaysAgoScores = new MorningNoonEvening(3.0, 1.0, 2.0);
        MorningNoonEvening fourDaysAgoScores = new MorningNoonEvening(1.0, 2.0, 0.0);
        MorningNoonEvening threeDaysAgoScores = new MorningNoonEvening(-1.0, 0.0, 1.0);
        MorningNoonEvening twoDaysAgoScores = new MorningNoonEvening(6.0, 3.0, 3.0);
        MorningNoonEvening yesterdayScores = new MorningNoonEvening(-3.0, -4.0, -2.0);

        ClothingData sixDaysAgoClothingData = new ClothingData(sixDaysAgo, sixDaysAgoFeels,
                sixDaysAgoCorrections, sixDaysAgoScores);
        ClothingData fiveDaysAgoClothingData = new ClothingData(fiveDaysAgo, fiveDaysAgoFeels,
                fiveDaysAgoCorrections, fiveDaysAgoScores);
        ClothingData fourDaysAgoClothingData = new ClothingData(fourDaysAgo, fourDaysAgoFeels,
                fourDaysAgoCorrections, fourDaysAgoScores);
        ClothingData threeDaysAgoClothingData = new ClothingData(threeDaysAgo, threeDaysAgoFeels,
                threeDaysAgoCorrections, threeDaysAgoScores);
        ClothingData twoDaysAgoClothingData = new ClothingData(twoDaysAgo, twoDaysAgoFeels,
                twoDaysAgoCorrections, twoDaysAgoScores);
        ClothingData yesterdayClothingData = new ClothingData(yesterday, yesterdayFeels,
                yesterdayCorrections, yesterdayScores);

        List<ClothingData> clothingDataList = Arrays.asList(
                sixDaysAgoClothingData, fiveDaysAgoClothingData, fourDaysAgoClothingData,
                threeDaysAgoClothingData, twoDaysAgoClothingData, yesterdayClothingData
        );

        OneUserData oneUserData = new OneUserData(123L, clothingDataList);

        ClothingPredictionRequest request = new ClothingPredictionRequest(
                Arrays.asList(oneUserData), "어제는 조금 추웠다.");

        client.getPrediction(request);
    }

    @Test
    void testGetPrediction() {
    }
}