package com.bond.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NoteResponseDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt,
        List<String> tags
) {
}
