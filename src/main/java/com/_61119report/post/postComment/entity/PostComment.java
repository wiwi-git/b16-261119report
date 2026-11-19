package com._61119report.post.postComment.entity;

import com._61119report.global.entity.BaseEntity;
import com._61119report.member.member.entity.Member;
import com._61119report.post.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * 댓글은 Post 를 통해서만 생성/삭제한다 ({@link Post#addComment}, {@link Post#removeComment}).
 */
@Entity
public class PostComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false, length = 1000)
    private String content;

    protected PostComment() {
    }

    public PostComment(Post post, Member author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public String getContent() {
        return content;
    }

    public Member getAuthor() {
        return author;
    }
}
