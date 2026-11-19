package com._61119report.member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberJoinRequest(
        @NotBlank @Size(min = 4, max = 30) String username,
        @NotBlank @Size(min = 8, max = 30) String password,
        @NotBlank @Size(min = 2, max = 30) String name
) {
}
