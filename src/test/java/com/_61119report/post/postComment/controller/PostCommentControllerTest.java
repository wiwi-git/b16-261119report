package com._61119report.post.postComment.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com._61119report.post.post.entity.Post;
import com._61119report.post.post.repository.PostRepository;
import com._61119report.post.post.service.PostService;
import com._61119report.post.postComment.dto.PostCommentCreateRequest;
import com._61119report.post.postComment.dto.PostCommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostCommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Post post;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        post = postRepository.save(new Post("제목", "내용", "yeon"));
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"댓글","author":"kim"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.statusCode").doesNotExist())
                .andExpect(jsonPath("$.msg").value(endsWith("번 댓글이 작성되었습니다.")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.content").value("댓글"))
                .andExpect(jsonPath("$.data.author").value("kim"));

        assertThat(commentCount()).isEqualTo(1);
    }

    @Test
    void createOnMissingPost() throws Exception {
        mockMvc.perform(post("/api/posts/{postId}/comments", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"댓글","author":"kim"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception {
        addComment("c1");
        addComment("c2");

        mockMvc.perform(get("/api/posts/{postId}/comments", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("c1"))
                .andExpect(jsonPath("$[1].content").value("c2"));
    }

    @Test
    void deleteComment() throws Exception {
        PostCommentResponse comment = addComment("c1");

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), comment.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 삭제되었습니다.".formatted(comment.id())))
                .andExpect(jsonPath("$.data").doesNotExist());

        assertThat(commentCount()).isZero();
    }

    @Test
    void deleteMissingComment() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCommentOfOtherPost() throws Exception {
        PostCommentResponse comment = addComment("c1");
        Post other = postRepository.save(new Post("다른 글", "내용", "lee"));

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", other.getId(), comment.id()))
                .andExpect(status().isNotFound());

        assertThat(commentCount()).isEqualTo(1);
    }

    @Test
    void deletingPostRemovesComments() throws Exception {
        addComment("c1");
        addComment("c2");

        mockMvc.perform(delete("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk());

        assertThat(commentCount()).isZero();
    }

    private PostCommentResponse addComment(String content) {
        return postService.addComment(post.getId(), new PostCommentCreateRequest(content, "kim"));
    }

    private int commentCount() {
        return jdbcTemplate.queryForObject("select count(*) from post_comment", Integer.class);
    }
}
