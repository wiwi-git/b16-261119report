package com._61119report.post.postComment.entity;

import com._61119report.global.entity.BaseEntity;
import com._61119report.global.exception.AccessDeniedException;
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

    public void modify(String content) {
        this.content = content;
    }

    public void checkActorCanModify(Member actor) {
        if (!isAuthor(actor)) {
            throw new AccessDeniedException();
        }
    }

    public void checkActorCanDelete(Member actor) {
        if (!isAuthor(actor)) {
            throw new AccessDeniedException();
        }
    }

    private boolean isAuthor(Member actor) {
        return author.getId().equals(actor.getId());
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
