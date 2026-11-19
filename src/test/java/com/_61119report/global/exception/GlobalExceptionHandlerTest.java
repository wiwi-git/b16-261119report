package com._61119report.global.exception;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(GlobalExceptionHandlerTest.ExceptionTestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * 아직 해당 예외를 던지는 기능(회원/actorId)이 없으므로 테스트 전용 엔드포인트로 검증한다.
     */
    @RestController
    @RequestMapping("/test/exceptions")
    static class ExceptionTestController {

        @GetMapping("/duplicate-username")
        void duplicateUsername() {
            throw new MemberDuplicateUsernameException("user1");
        }

        @GetMapping("/unauthenticated")
        void unauthenticated() {
            throw new UnauthenticatedException();
        }

        @GetMapping("/access-denied")
        void accessDenied() {
            throw new AccessDeniedException();
        }
    }

    @Test
    void validationErrorMessageIsFieldCodeMessageSortedByField() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","content":"","author":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.statusCode").doesNotExist())
                // defaultMessage 는 로케일마다 달라서 "필드-코드-" 와 정렬/줄바꿈만 검증
                .andExpect(jsonPath("$.msg").value(matchesPattern(
                        "author-NotBlank-.+\ncontent-NotBlank-.+\ntitle-NotBlank-.+")));
    }

    @Test
    void malformedJson() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-2"));
    }

    @Test
    void postNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("9999번 글은 존재하지 않습니다."));
    }

    @Test
    void memberDuplicateUsername() throws Exception {
        mockMvc.perform(get("/test/exceptions/duplicate-username"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("user1(은)는 이미 사용중인 username 입니다."));
    }

    @Test
    void unauthenticated() throws Exception {
        mockMvc.perform(get("/test/exceptions/unauthenticated"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }

    @Test
    void accessDenied() throws Exception {
        mockMvc.perform(get("/test/exceptions/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("권한이 없습니다."));
    }
}
