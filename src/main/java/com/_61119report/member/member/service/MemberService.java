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
        return join(reqBody.username(), reqBody.password(), reqBody.name());
    }

    // 요청 DTO 검증(@Valid)을 거치지 않는 내부용 (샘플 데이터 등)
    @Transactional
    public Member join(String username, String password, String name) {
        if (memberRepository.existsByUsername(username)) {
            throw new MemberDuplicateUsernameException(username);
        }

        return memberRepository.save(new Member(username, password, name));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    /**
     * actorId 로 현재 사용자를 찾는다. actorId 가 없거나 존재하지 않는 회원이면 인증 실패(401).
     */
    @Transactional(readOnly = true)
    public MemberDto findMe(Long actorId) {
        return MemberDto.from(getActor(actorId));
    }

    /**
     * actorId 에 해당하는 회원 엔티티. 글/댓글 작성자 지정 등에 사용.
     * actorId 가 없거나 존재하지 않는 회원이면 인증 실패(401).
     */
    @Transactional(readOnly = true)
    public Member getActor(Long actorId) {
        if (actorId == null) {
            throw new UnauthenticatedException();
        }

        return memberRepository.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
    }
}
