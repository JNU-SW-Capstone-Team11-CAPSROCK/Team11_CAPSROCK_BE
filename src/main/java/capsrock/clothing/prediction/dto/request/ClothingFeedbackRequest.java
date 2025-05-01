package capsrock.clothing.prediction.dto.request;

public record ClothingFeedbackRequest(
        Integer morningScore, Integer noonScore, Integer eveningScore, String comment
) {

}
