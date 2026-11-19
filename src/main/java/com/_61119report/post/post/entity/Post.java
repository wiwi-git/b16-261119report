package com._61119report.post.post.entity;

import com._61119report.global.entity.BaseEntity;
import com._61119report.global.exception.AccessDeniedException;
import com._61119report.global.exception.PostCommentNotFoundException;
import com._61119report.member.member.entity.Member;
import com._61119report.post.postComment.entity.PostComment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // 댓글의 생명주기는 Post 가 관리한다: 컬렉션에 추가/제거하면 더티체킹으로 INSERT/DELETE 된다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PostComment> comments = new ArrayList<>();

    protected Post() {
    }

    public Post(Member author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostComment addComment(Member author, String content) {
        PostComment comment = new PostComment(this, author, content);
        comments.add(comment);
        return comment;
    }

    public void removeComment(Member actor, Long commentId) {
        PostComment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new PostCommentNotFoundException(commentId));
        comment.checkActorCanDelete(actor);
        comments.remove(comment);
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

    public List<PostComment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Member getAuthor() {
        return author;
    }
}
