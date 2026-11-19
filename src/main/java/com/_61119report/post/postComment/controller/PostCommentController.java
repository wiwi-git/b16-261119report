package com._61119report.post.postComment.controller;

import com._61119report.global.rsData.RsData;
import com._61119report.post.post.service.PostService;
import com._61119report.post.postComment.dto.PostCommentCreateRequest;
import com._61119report.post.postComment.dto.PostCommentResponse;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 댓글 전용 service/repository 없이 PostService 를 통해 Post 애그리거트로 처리한다.
 * RsData 반환 API 의 HTTP 상태코드는 ResponseAspect 가 rsData.statusCode() 로 지정한다.
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
    public RsData<PostCommentResponse> create(
            @PathVariable Long postId,
            @RequestBody PostCommentCreateRequest request
    ) {
        PostCommentResponse response = postService.addComment(postId, request);
        return new RsData<>("201-1", "%d번 댓글이 작성되었습니다.".formatted(response.id()), response);
    }

    @DeleteMapping("/{commentId}")
    public RsData<Void> delete(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(postId, commentId);
        return new RsData<>("200-1", "%d번 댓글이 삭제되었습니다.".formatted(commentId));
    }
}
