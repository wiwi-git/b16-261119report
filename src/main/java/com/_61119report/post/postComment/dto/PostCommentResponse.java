package com._61119report.post.postComment.dto;

import com._61119report.post.postComment.entity.PostComment;
import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long authorId,
        String authorName,
        String content
) {

    public static PostCommentResponse from(PostComment comment) {
        return new PostCommentResponse(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getContent()
        );
    }
}
