package com._61119report.post.postComment.entity;

import com._61119report.global.entity.BaseEntity;
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

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    protected PostComment() {
    }

    public PostComment(Post post, String content, String author) {
        this.post = post;
        this.content = content;
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}
