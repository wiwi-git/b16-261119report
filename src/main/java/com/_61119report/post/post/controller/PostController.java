package com._61119report.post.post.controller;

import com._61119report.global.rsData.RsData;
import com._61119report.post.post.dto.PostCreateRequest;
import com._61119report.post.post.dto.PostResponse;
import com._61119report.post.post.dto.PostUpdateRequest;
import com._61119report.post.post.service.PostService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<RsData<PostResponse>> create(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.create(request);
        RsData<PostResponse> rsData = new RsData<>(
                "201-1", "%d번 글이 작성되었습니다.".formatted(response.id()), response);

        return ResponseEntity.status(rsData.statusCode())
                .location(URI.create("/api/posts/" + response.id()))
                .body(rsData);
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
    public ResponseEntity<RsData<PostResponse>> update(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request
    ) {
        PostResponse response = postService.update(id, request);
        RsData<PostResponse> rsData = new RsData<>(
                "200-1", "%d번 글이 수정되었습니다.".formatted(id), response);

        return ResponseEntity.status(rsData.statusCode()).body(rsData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<Void>> delete(@PathVariable Long id) {
        postService.delete(id);
        RsData<Void> rsData = new RsData<>("200-1", "%d번 글이 삭제되었습니다.".formatted(id));

        return ResponseEntity.status(rsData.statusCode()).body(rsData);
    }
}
