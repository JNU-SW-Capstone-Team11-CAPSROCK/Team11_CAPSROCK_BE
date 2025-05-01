package capsrock.clothing.prediction.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record FeelsLikeTemp(
        Double morning, Double noon, Double evening
) {

}
