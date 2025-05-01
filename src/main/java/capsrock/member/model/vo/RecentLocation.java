package capsrock.member.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record RecentLocation(
        Double longitude, Double latitude
) {

}
