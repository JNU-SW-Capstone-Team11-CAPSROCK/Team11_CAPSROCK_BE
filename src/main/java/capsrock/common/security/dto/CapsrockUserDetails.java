package capsrock.common.security.dto;

import capsrock.member.dto.MemberInfoDTO;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CapsrockUserDetails implements UserDetails {
    private final MemberInfoDTO memberInfo;

    public MemberInfoDTO getMemberInfoDTO() {
        return memberInfo;
    }

    @Override public String getUsername() { return memberInfo.email().value(); }
    @Override public String getPassword() { return memberInfo.encryptedPassword().value(); }

    //사용자의 역할
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    //계정이 만료 됐는지 ex) 구독제
    @Override public boolean isAccountNonExpired() { return true; }

    //계정이 잠겼는지
    @Override public boolean isAccountNonLocked() { return true; }

    //비밀번호 만료 여부
    @Override public boolean isCredentialsNonExpired() { return true; }

    //계정이 활성화 됐는지
    @Override public boolean isEnabled() { return true; }
}
