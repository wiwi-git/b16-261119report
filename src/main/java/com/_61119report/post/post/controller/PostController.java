package com._61119report.post.post.controller;

import com._61119report.global.rsData.RsData;
import com._61119report.post.post.dto.PostCreateRequest;
import com._61119report.post.post.dto.PostResponse;
import com._61119report.post.post.dto.PostUpdateRequest;
import com._61119report.post.post.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RsData 반환 API 의 HTTP 상태코드는 ResponseAspect 가 rsData.statusCode() 로 지정한다.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public RsData<PostResponse> create(
            @RequestParam(required = false) Long actorId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.create(actorId, request);
        return new RsData<>("201-1", "%d번 글이 작성되었습니다.".formatted(response.id()), response);
    }

    @GetMapping
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PutMapping("/{id}")
    public RsData<PostResponse> update(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.update(id, request);
        return new RsData<>("200-1", "%d번 글이 수정되었습니다.".formatted(id), response);
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return new RsData<>("200-1", "%d번 글이 삭제되었습니다.".formatted(id));
    }
}
