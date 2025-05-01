package capsrock.clothing.prediction.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Score(
        Integer morningScore, Integer noonScore, Integer eveningScore
) {

    public Score {
        validate();
    }

    private void validate() {
        if (morningScore == null || noonScore == null || eveningScore == null) {
            throw new IllegalArgumentException("점수를 입력해주시기 바랍니다.");
        }

        if (morningScore < -10 || noonScore < -10 || eveningScore < -10 || morningScore > 10
                || noonScore > 10 || eveningScore > 10
        ) {
            throw new IllegalArgumentException("점수는 -10 ~ 10점 사이여야 합니다.");
        }
    }
}