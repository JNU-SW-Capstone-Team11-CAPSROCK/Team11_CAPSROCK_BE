package capsrock.clothing.client;

import capsrock.clothing.dto.client.request.ClothingData;
import capsrock.clothing.dto.client.request.ClothingPredictionRequest;
import capsrock.clothing.dto.client.request.OneUserData;
import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Score;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClothingGeminiClientTest {

    @Autowired
    private ClothingGeminiClient client;

    @Test
    void getPrediction() throws JsonProcessingException, ExecutionException, InterruptedException {
        LocalDate now = LocalDate.now();
        List<ClothingData> clothingDataList = createLastDaysClothingData(now, 6);

        OneUserData oneUserData = new OneUserData(123L, clothingDataList,
                new FeelsLikeTemp(10.0, 20.0, 10.0));

        ClothingPredictionRequest request = new ClothingPredictionRequest(
                List.of(oneUserData));

        System.out.println("client.getPrediction(request) = " + client.getPrediction(request).get());
    }

    /**
     * 지정된 날짜부터 과거 n일 동안의 의류 데이터를 생성합니다.
     *
     * @param baseDate 기준 날짜
     * @param days     생성할 과거 일수
     * @return 날짜 역순으로 정렬된 의류 데이터 목록 (과거 → 현재)
     */
    private List<ClothingData> createLastDaysClothingData(LocalDate baseDate, int days) {
        List<ClothingData> result = new ArrayList<>();

        for (int daysAgo = days; daysAgo > 0; daysAgo--) {
            LocalDate date = baseDate.minusDays(daysAgo);
            result.add(createClothingDataForDate(date, daysAgo));
        }

        return result;
    }

    /**
     * 특정 날짜에 대한 의류 데이터를 생성합니다. 참고: 실제 테스트 데이터는 단순화를 위해 날짜 인덱스에 따라 다른 값을 할당
     */
    private ClothingData createClothingDataForDate(LocalDate date, int daysAgo) {
        // 각 날짜별 데이터 매핑
        FeelsLikeTemp feelsLikeTemp;
        Correction correction;
        Score score;

        switch (daysAgo) {
            case 6:
                feelsLikeTemp = new FeelsLikeTemp(12.0, 19.4, 13.2);
                correction = new Correction(3.1, 2.5, 1.6);
                score = new Score(0, -1, -1);
                break;
            case 5:
                feelsLikeTemp = new FeelsLikeTemp(10.0, 16.1, 12.2);
                correction = new Correction(-1.9, -0.2, 1.5);
                score = new Score(3, 1, 2);
                break;
            case 4:
                feelsLikeTemp = new FeelsLikeTemp(13.0, 15.4, 10.2);
                correction = new Correction(0.1, 0.3, -0.1);
                score = new Score(1, 2, 0);
                break;
            case 3:
                feelsLikeTemp = new FeelsLikeTemp(9.4, 13.4, 3.2);
                correction = new Correction(2.0, 1.4, 0.4);
                score = new Score(-1, 0, 1);
                break;
            case 2:
                feelsLikeTemp = new FeelsLikeTemp(5.1, 12.4, 7.8);
                correction = new Correction(0.3, -0.5, 1.0);
                score = new Score(6, 3, 3);
                break;
            case 1:
                feelsLikeTemp = new FeelsLikeTemp(8.6, 21.4, 11.2);
                correction = new Correction(0.4, 0.3, 0.4);
                score = new Score(-3, -4, -2);
                break;
            default:
                feelsLikeTemp = new FeelsLikeTemp(0.0, 0.0, 0.0);
                correction = new Correction(0.0, 0.0, 0.0);
                score = new Score(0, 0, 0);
        }

        return new ClothingData(date, feelsLikeTemp, correction, score, "");
    }
}