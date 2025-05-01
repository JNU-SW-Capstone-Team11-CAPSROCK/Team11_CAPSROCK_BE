package capsrock.clothing.prediction.repository;

import capsrock.clothing.prediction.model.entity.ClothingPrediction;
import capsrock.clothing.prediction.model.vo.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingPredictionRepository extends JpaRepository<ClothingPrediction, Long> {
    Optional<ClothingPrediction> findByMemberId(Long memberId);

    Boolean existsByMemberIdAndStatus(Long memberId, Status status);
}
