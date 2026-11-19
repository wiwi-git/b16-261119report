package com._61119report.member.member.controller;

import com._61119report.global.rsData.RsData;
import com._61119report.member.member.dto.MemberJoinRequest;
import com._61119report.member.member.entity.Member;
import com._61119report.member.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public RsData<Void> join(@Valid @RequestBody MemberJoinRequest reqBody) {
        Member member = memberService.join(reqBody);
        return new RsData<>("201-1", "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()));
    }
}
