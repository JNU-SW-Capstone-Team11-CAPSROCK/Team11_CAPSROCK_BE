package capsrock.clothing.model.vo;


import jakarta.persistence.Embeddable;

@Embeddable
public record Location(
        Double longitude, Double latitude
) {

}
