package capsrock.clothing.prediction.model.entity;

import capsrock.clothing.prediction.model.vo.Correction;
import capsrock.clothing.prediction.model.vo.FeelsLikeTemp;
import capsrock.clothing.prediction.model.vo.Location;
import capsrock.clothing.prediction.model.vo.Score;
import capsrock.clothing.prediction.model.vo.Status;
import capsrock.common.model.BaseEntity;
import capsrock.member.model.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothingPrediction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    
    //상태 -> 1. 피드백 대기중(PENDING) 2. 피드백 완료되고 저장 대기 중(COMPLETED) 3. 저장 (ARCHIVED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    //예측 시간
    @Column(nullable = false)
    private LocalDate predictedAt;
    
    //보정치
    @Embedded
    @Column(nullable = false)
    private Correction correction;
    
    //체감온도 예측 좌표
    @Setter
    @Embedded
    @Column(nullable = false)
    private Location location;
    
    //해당 좌표의 체감온도
    @Setter
    @Embedded
    @Column(nullable = false)
    private FeelsLikeTemp feelsLikeTemp;
    
    //피드백 점수
    @Setter
    @Embedded
    @Column(nullable = true)
    private Score score;
    
    //피드백 comment
    @Setter
    @Column(nullable = true, length = 3000)
    private String comment;

    //피드백 받은 시간
    @Column(nullable = true)
    private LocalDateTime feedbackReceivedAt;


    @Builder
    public ClothingPrediction(Member member, Correction correction, Location location,
            LocalDate predictedAt, FeelsLikeTemp feelsLikeTemp) {
        this.member = member;
        this.correction = correction;
        this.location = location;
        this.predictedAt = predictedAt;
        this.feelsLikeTemp = feelsLikeTemp;
        this.status = Status.PENDING;
    }

    public void receiveFeedback(Score score, String comment, LocalDateTime feedbackReceivedAt) {
        comment = comment == null ? "" : comment;

        this.feedbackReceivedAt = feedbackReceivedAt;
        this.score = score;
        this.comment = comment;
        this.changeStatusCompleted();
    }

    public void changeToArchive() {
        this.changeStatusArchived();
    }

    private void changeStatusCompleted() {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 COMPLETED로 바꿀 수 있습니다.");
        }

        this.status = Status.COMPLETED;
    }

    private void changeStatusArchived() {
        if (this.status != Status.COMPLETED) {
            throw new IllegalStateException("COMPLETED 상태에서만 ARCHIVED로 바꿀 수 있습니다.");
        }

        this.status = Status.ARCHIVED;
    }

}
