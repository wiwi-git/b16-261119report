package com._61119report.post.post.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"제목","content":"내용","author":"yeon"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.author").value("yeon"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void findAll() throws Exception {
        postRepository.save(new Post("t1", "c1", "a"));
        postRepository.save(new Post("t2", "c2", "b"));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findById() throws Exception {
        Post saved = postRepository.save(new Post("t1", "c1", "a"));

        mockMvc.perform(get("/api/posts/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("t1"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        Post saved = postRepository.save(new Post("t1", "c1", "a"));

        mockMvc.perform(put("/api/posts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"수정 제목","content":"수정 내용"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 제목"))
                .andExpect(jsonPath("$.content").value("수정 내용"));
    }

    @Test
    void deletePost() throws Exception {
        Post saved = postRepository.save(new Post("t1", "c1", "a"));

        mockMvc.perform(delete("/api/posts/{id}", saved.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/posts/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
