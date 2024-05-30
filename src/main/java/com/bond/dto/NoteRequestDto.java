package com.bond.dto;

import jakarta.validation.constraints.NotEmpty;

public record NoteRequestDto(
        @NotEmpty
        String title,
        String content
) {
}
