package capsrock.clothing.prediction.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Correction(Double morningCorrection, Double noonCorrection,
                         Double eveningCorrection) {
}
