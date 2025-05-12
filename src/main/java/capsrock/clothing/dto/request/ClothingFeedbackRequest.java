package capsrock.clothing.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record ClothingFeedbackRequest(
        @NotNull(message = "아침 점수를 입력해주세요.")
        @Min(value = -10, message = "아침 점수는 -10 이상이어야 합니다.")
        @Max(value = 10, message = "아침 점수는 10 이하이어야 합니다.")
        Integer morningScore,

        @NotNull(message = "점심 점수를 입력해주세요.")
        @Min(value = -10, message = "점심 점수는 -10 이상이어야 합니다.")
        @Max(value = 10, message = "점심 점수는 10 이하이어야 합니다.")
        Integer noonScore,

        @NotNull(message = "저녁 점수를 입력해주세요.")
        @Min(value = -10, message = "저녁 점수는 -10 이상이어야 합니다.")
        @Max(value = 10, message = "저녁 점수는 10 이하이어야 합니다.")
        Integer eveningScore,

        @Nullable
        String comment
) {

}
