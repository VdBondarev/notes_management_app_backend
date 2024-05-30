package com.bond.dto;

public record CreateNoteRequestDto(
        String title,
        String content
) {
}
