package com._61119report.post.post.entity;

import com._61119report.global.entity.BaseEntity;
import com._61119report.global.exception.PostCommentNotFoundException;
import com._61119report.post.postComment.entity.PostComment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    // 댓글의 생명주기는 Post 가 관리한다: 컬렉션에 추가/제거하면 더티체킹으로 INSERT/DELETE 된다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PostComment> comments = new ArrayList<>();

    protected Post() {
    }

    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostComment addComment(String content, String author) {
        PostComment comment = new PostComment(this, content, author);
        comments.add(comment);
        return comment;
    }

    public void removeComment(Long commentId) {
        PostComment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new PostCommentNotFoundException(commentId));
        comments.remove(comment);
    }

    public List<PostComment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}
