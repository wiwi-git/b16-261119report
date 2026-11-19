package com._61119report.member.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com._61119report.member.member.entity.Member;
import com._61119report.member.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void join() throws Exception {
        memberRepository.findByUsername("newuser").ifPresent(memberRepository::delete);

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"newuser","password":"password1","name":"홍길동"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("홍길동님 환영합니다. 회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void joinDuplicateUsername() throws Exception {
        // user1 은 샘플 데이터(BaseInitData)로 이미 존재
        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user1","password":"password2","name":"임꺽정"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("user1(은)는 이미 사용중인 username 입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void me() throws Exception {
        Member member = memberRepository.findByUsername("user1").orElseThrow();

        mockMvc.perform(get("/api/v1/members/me").param("actorId", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.createDate").exists())
                .andExpect(jsonPath("$.updateDate").exists())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.name").value("유저1"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void meWithoutActorId() throws Exception {
        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"));
    }

    @Test
    void joinInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"abc","password":"short","name":"홍"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"));
    }
}
