package capsrock.member.model.entity;

import capsrock.common.model.BaseEntity;
import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;
import capsrock.member.model.vo.Nickname;
import capsrock.member.model.vo.RecentLocation;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false))
    private Nickname nickname;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private EncryptedPassword encryptedPassword;

    @Embedded
    @Column(nullable = false)
    private RecentLocation recentLocation;

    @Builder
    public Member(String email, String nickname, String encryptedPassword, Double longitude,
            Double latitude) {
        this.email = new Email(email);
        this.nickname = new Nickname(nickname);
        this.encryptedPassword = new EncryptedPassword(encryptedPassword);
        this.recentLocation = new RecentLocation(longitude, latitude);
    }

    public void updateLocation(Double longitude, Double latitude) {
        this.recentLocation = new RecentLocation(longitude, latitude);
    }
}