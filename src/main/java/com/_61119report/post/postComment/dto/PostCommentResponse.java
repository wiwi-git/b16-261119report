package com._61119report.post.postComment.dto;

import com._61119report.post.postComment.entity.PostComment;
import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        String content,
        String author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostCommentResponse from(PostComment comment) {
        return new PostCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
