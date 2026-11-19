package com._61119report.post.postComment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCommentCreateRequest(
        @NotBlank @Size(max = 1000) String content,
        @NotBlank @Size(max = 50) String author
) {
}
