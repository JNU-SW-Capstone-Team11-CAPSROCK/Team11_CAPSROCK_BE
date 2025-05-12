package capsrock.clothing.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ClothingPageRequest(
        @NotNull(message = "경도를 입력해주세요.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 과 180 사이여야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 -180 과 180 사이여야 합니다.")
        Double longitude,

        @NotNull(message = "위도를 입력해주세요.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 과 90 사이여야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 -90 과 90 사이여야 합니다.")
        Double latitude
) {

}
