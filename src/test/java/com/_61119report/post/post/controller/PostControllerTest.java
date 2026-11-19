package com._61119report.post.post.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com._61119report.member.member.entity.Member;
import com._61119report.member.member.repository.MemberRepository;
import com._61119report.post.post.entity.Post;
import com._61119report.post.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
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
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    Member author;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        author = memberRepository.findByUsername("user1").orElseThrow(); // 샘플 회원
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .param("actorId", author.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"제목","content":"내용"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.statusCode").doesNotExist())
                .andExpect(jsonPath("$.msg").value(endsWith("번 글이 작성되었습니다.")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.authorId").value(author.getId()))
                .andExpect(jsonPath("$.data.authorName").value("유저1"))
                .andExpect(jsonPath("$.data.createdAt").exists());
    }

    @Test
    void createWithoutActorId() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"제목","content":"내용"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"));
    }

    @Test
    void findAll() throws Exception {
        postRepository.save(new Post(author, "t1", "c1"));
        postRepository.save(new Post(author, "t2", "c2"));

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findById() throws Exception {
        Post saved = postRepository.save(new Post(author, "t1", "c1"));

        mockMvc.perform(get("/api/v1/posts/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("t1"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/posts/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        Post saved = postRepository.save(new Post(author, "t1", "c1"));

        mockMvc.perform(put("/api/v1/posts/{id}", saved.getId())
                        .param("actorId", author.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"수정 제목","content":"수정 내용"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 수정되었습니다.".formatted(saved.getId())))
                .andExpect(jsonPath("$.data.title").value("수정 제목"))
                .andExpect(jsonPath("$.data.content").value("수정 내용"));
    }

    @Test
    void updateByNonAuthor() throws Exception {
        Post saved = postRepository.save(new Post(author, "t1", "c1"));
        Member other = memberRepository.findByUsername("user2").orElseThrow();

        mockMvc.perform(put("/api/v1/posts/{id}", saved.getId())
                        .param("actorId", other.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"수정 제목","content":"수정 내용"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("권한이 없습니다."));
    }

    @Test
    void deletePost() throws Exception {
        Post saved = postRepository.save(new Post(author, "t1", "c1"));

        mockMvc.perform(delete("/api/v1/posts/{id}", saved.getId())
                        .param("actorId", author.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(saved.getId())))
                .andExpect(jsonPath("$.data").doesNotExist());
        mockMvc.perform(get("/api/v1/posts/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
