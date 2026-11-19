package com._61119report.member.member.service;

import com._61119report.global.exception.MemberDuplicateUsernameException;
import com._61119report.global.exception.UnauthenticatedException;
import com._61119report.member.member.dto.MemberDto;
import com._61119report.member.member.dto.MemberJoinRequest;
import com._61119report.member.member.entity.Member;
import com._61119report.member.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member join(MemberJoinRequest reqBody) {
        if (memberRepository.existsByUsername(reqBody.username())) {
            throw new MemberDuplicateUsernameException(reqBody.username());
        }

        return memberRepository.save(new Member(reqBody.username(), reqBody.password(), reqBody.name()));
    }

    /**
     * actorId 로 현재 사용자를 찾는다. actorId 가 없거나 존재하지 않는 회원이면 인증 실패(401).
     */
    @Transactional(readOnly = true)
    public MemberDto findMe(Long actorId) {
        if (actorId == null) {
            throw new UnauthenticatedException();
        }

        return memberRepository.findById(actorId)
                .map(MemberDto::from)
                .orElseThrow(UnauthenticatedException::new);
    }
}
