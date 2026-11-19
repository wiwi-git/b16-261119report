package com._61119report.post.post.service;

import com._61119report.global.exception.PostNotFoundException;
import com._61119report.member.member.entity.Member;
import com._61119report.member.member.service.MemberService;
import com._61119report.post.post.dto.PostCreateRequest;
import com._61119report.post.post.dto.PostResponse;
import com._61119report.post.post.dto.PostUpdateRequest;
import com._61119report.post.post.entity.Post;
import com._61119report.post.post.repository.PostRepository;
import com._61119report.post.postComment.dto.PostCommentCreateRequest;
import com._61119report.post.postComment.dto.PostCommentResponse;
import com._61119report.post.postComment.entity.PostComment;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;

    public PostService(PostRepository postRepository, MemberService memberService) {
        this.postRepository = postRepository;
        this.memberService = memberService;
    }

    @Transactional
    public PostResponse create(Long actorId, PostCreateRequest request) {
        Member author = memberService.getActor(actorId);
        Post post = new Post(author, request.title(), request.content());
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        return PostResponse.from(getPost(id));
    }

    @Transactional
    public PostResponse update(Long id, Long actorId, PostUpdateRequest request) {
        Member actor = memberService.getActor(actorId);
        Post post = getPost(id);
        post.checkActorCanModify(actor);
        post.update(request.title(), request.content());
        postRepository.flush();
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long id, Long actorId) {
        Member actor = memberService.getActor(actorId);
        Post post = getPost(id);
        post.checkActorCanDelete(actor);
        postRepository.delete(post);
    }

    // ===== 댓글: Post 애그리거트를 통해 처리 (save/delete 호출 없이 더티체킹) =====

    @Transactional(readOnly = true)
    public List<PostCommentResponse> findComments(Long postId) {
        return getPost(postId).getComments().stream()
                .map(PostCommentResponse::from)
                .toList();
    }

    @Transactional
    public PostCommentResponse addComment(Long postId, Long actorId, PostCommentCreateRequest request) {
        Member author = memberService.getActor(actorId);
        Post post = getPost(postId);
        PostComment comment = post.addComment(author, request.content());
        postRepository.flush(); // 응답에 담을 댓글 id 를 받기 위해 즉시 반영
        return PostCommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long actorId) {
        Member actor = memberService.getActor(actorId);
        Post post = getPost(postId);
        post.removeComment(actor, commentId); // 작성자 확인 후 컬렉션에서 제거 → orphanRemoval 로 커밋 시 DELETE
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
}
