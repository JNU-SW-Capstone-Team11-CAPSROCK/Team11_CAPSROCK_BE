package capsrock.member.controller;

import capsrock.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<?> getMemberInfo(/*@LoginMember MemberInfoDTO memberInfoDTO*/) {
//        return ResponseEntity.ok(memberService.login(memberDTO));
        return ResponseEntity.ok().build();
    }
}
