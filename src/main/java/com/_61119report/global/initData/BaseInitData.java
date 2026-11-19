package com._61119report.global.initData;

import com._61119report.member.member.service.MemberService;
import com._61119report.post.post.dto.PostCreateRequest;
import com._61119report.post.post.service.PostService;
import com._61119report.post.postComment.dto.PostCommentCreateRequest;
import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 샘플 데이터. 애플리케이션 시작 시 순서대로 생성한다.
 * - 회원 6명: 이미 있는 username 은 건너뛴다.
 * - 글 3개 / 댓글 5개: 글이 하나도 없을 때만 생성한다.
 * 여러 번 실행해도 중복 생성되지 않는다.
 */
@Configuration
public class BaseInitData {

    private static final String SAMPLE_PASSWORD = "12345678";

    private record SampleMember(String username, String name) {
    }

    private record SamplePost(String authorUsername, String title, String content) {
    }

    // postNo: SAMPLE_POSTS 의 몇 번째 글(1부터)에 다는지
    private record SampleComment(int postNo, String authorUsername, String content) {
    }

    private static final List<SampleMember> SAMPLE_MEMBERS = List.of(
            new SampleMember("system", "시스템"),
            new SampleMember("admin", "관리자"),
            new SampleMember("user1", "유저1"),
            new SampleMember("user2", "유저2"),
            new SampleMember("user3", "유저3"),
            new SampleMember("user4", "유저4")
    );

    private static final List<SamplePost> SAMPLE_POSTS = List.of(
            new SamplePost("user1", "제목 1", "내용 1"),
            new SamplePost("user2", "제목 2", "내용 2"),
            new SamplePost("user3", "제목 3", "내용 3")
    );

    private static final List<SampleComment> SAMPLE_COMMENTS = List.of(
            new SampleComment(1, "user1", "댓글 1"),
            new SampleComment(1, "user2", "댓글 2"),
            new SampleComment(1, "user3", "댓글 3"),
            new SampleComment(2, "user1", "댓글 4"),
            new SampleComment(2, "user2", "댓글 5")
    );

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner(MemberService memberService, PostService postService) {
        return args -> {
            initMembers(memberService);
            initPostsAndComments(memberService, postService);
        };
    }

    private void initMembers(MemberService memberService) {
        SAMPLE_MEMBERS.stream()
                .filter(sample -> !memberService.existsByUsername(sample.username()))
                .forEach(sample -> memberService.join(sample.username(), SAMPLE_PASSWORD, sample.name()));
    }

    private void initPostsAndComments(MemberService memberService, PostService postService) {
        if (postService.count() > 0) {
            return;
        }

        List<Long> postIds = SAMPLE_POSTS.stream()
                .map(sample -> postService.create(
                        actorId(memberService, sample.authorUsername()),
                        new PostCreateRequest(sample.title(), sample.content())
                ).id())
                .toList();

        SAMPLE_COMMENTS.forEach(sample -> postService.addComment(
                postIds.get(sample.postNo() - 1),
                actorId(memberService, sample.authorUsername()),
                new PostCommentCreateRequest(sample.content())
        ));
    }

    private Long actorId(MemberService memberService, String username) {
        return memberService.findByUsername(username).orElseThrow().getId();
    }
}
