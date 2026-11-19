package com._61119report.post.postComment.controller;

import com._61119report.post.post.service.PostService;
import com._61119report.post.postComment.dto.PostCommentCreateRequest;
import com._61119report.post.postComment.dto.PostCommentResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 댓글 전용 service/repository 없이 PostService 를 통해 Post 애그리거트로 처리한다.
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class PostCommentController {

    private final PostService postService;

    public PostCommentController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostCommentResponse> findAll(@PathVariable Long postId) {
        return postService.findComments(postId);
    }

    @PostMapping
    public ResponseEntity<PostCommentResponse> create(
            @PathVariable Long postId,
            @RequestBody PostCommentCreateRequest request
    ) {
        PostCommentResponse response = postService.addComment(postId, request);
        return ResponseEntity
                .created(URI.create("/api/posts/" + postId + "/comments/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
