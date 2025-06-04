package capsrock.clothing.repository;

import capsrock.clothing.model.entity.ClothingPrediction;
import capsrock.clothing.model.vo.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingPredictionRepository extends JpaRepository<ClothingPrediction, Long> {
    Optional<ClothingPrediction> findByMemberId(Long memberId);
    ClothingPrediction findByMemberIdAndStatus(Long memberId, Status status);

    Boolean existsByMemberIdAndStatus(Long memberId, Status status);

    List<Long> getDistinctMemberIdsByStatus(Status status);
}
