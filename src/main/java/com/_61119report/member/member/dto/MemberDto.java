package com._61119report.member.member.dto;

import com._61119report.member.member.entity.Member;
import java.time.LocalDateTime;

/**
 * 회원 정보 응답. 비밀번호는 포함하지 않는다.
 */
public record MemberDto(
        Long id,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        String username,
        String name
) {

    public static MemberDto from(Member member) {
        return new MemberDto(
                member.getId(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getUsername(),
                member.getName()
        );
    }
}
