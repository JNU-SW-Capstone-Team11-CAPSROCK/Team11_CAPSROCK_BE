package capsrock.member.model.entity;

import capsrock.member.model.vo.Email;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;

//    private List<> aiPredictedFeelTemperatures;

    protected Member() {
    }

    public Member(String email) {
        this.email = new Email(email);
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }
}