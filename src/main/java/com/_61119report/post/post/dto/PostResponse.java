package com._61119report.post.post.dto;

import com._61119report.post.post.entity.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long authorId,
        String authorName,
        String title,
        String content
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getTitle(),
                post.getContent()
        );
    }
}
