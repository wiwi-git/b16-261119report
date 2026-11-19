package com._61119report.global.initData;

import com._61119report.member.member.service.MemberService;
import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 샘플 데이터. 애플리케이션 시작 시 기본 회원 6명이 DB 에 없으면 순서대로 생성한다.
 * 이미 있는 username 은 건너뛰므로 여러 번 실행해도 중복 생성되지 않는다.
 */
@Configuration
public class BaseInitData {

    private static final String SAMPLE_PASSWORD = "12345678";

    private record SampleMember(String username, String name) {
    }

    private static final List<SampleMember> SAMPLE_MEMBERS = List.of(
            new SampleMember("system", "시스템"),
            new SampleMember("admin", "관리자"),
            new SampleMember("user1", "유저1"),
            new SampleMember("user2", "유저2"),
            new SampleMember("user3", "유저3"),
            new SampleMember("user4", "유저4")
    );

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner(MemberService memberService) {
        return args -> SAMPLE_MEMBERS.stream()
                .filter(sample -> !memberService.existsByUsername(sample.username()))
                .forEach(sample -> memberService.join(sample.username(), SAMPLE_PASSWORD, sample.name()));
    }
}
