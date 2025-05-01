package capsrock.member.dto.service;

import capsrock.member.model.entity.Member;
import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;
import capsrock.member.model.vo.Nickname;
import capsrock.member.model.vo.RecentLocation;

public record MemberInfoDTO(
        Long id, Email email, Nickname nickname, EncryptedPassword encryptedPassword,
        RecentLocation recentLocation
) {

    public static MemberInfoDTO from(Member member) {
        return new MemberInfoDTO(member.getId(), member.getEmail(), member.getNickname(),
                member.getEncryptedPassword(), member.getRecentLocation());
    }

//    public Member toEntity() {
//        return Member
//                .builder()
//                .nickname(this.nickname.value())
//                .email(this.email.value())
//                .encryptedPassword(this.encryptedPassword.value())
//                .build();
//    }
}
