package capsrock.member.model.entity;

import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;
import capsrock.member.model.vo.Nickname;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
public class Member {

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

    @Builder
    public Member(String email, String nickname, String encryptedPassword) {
        this.email = new Email(email);
        this.nickname = new Nickname(nickname);
        this.encryptedPassword = new EncryptedPassword(encryptedPassword);
    }
//    private List<> aiPredictedFeelTemperatures;

}