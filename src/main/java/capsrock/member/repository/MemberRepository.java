package capsrock.member.repository;


import capsrock.member.model.entity.Member;
import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(Email email);

    Optional<Member> findByEmail(Email email);

    Optional<Member> findByEmailAndEncryptedPassword(Email email, EncryptedPassword encryptedPassword);
}
