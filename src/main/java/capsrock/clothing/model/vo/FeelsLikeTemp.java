package capsrock.clothing.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record FeelsLikeTemp(
        Double morning, Double noon, Double evening
) {

}
