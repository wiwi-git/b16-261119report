package com._61119report.post.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        @NotBlank @Size(max = 50) String author
) {
}
