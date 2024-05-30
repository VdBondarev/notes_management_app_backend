package com.bond.controller;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notes controller",
        description = "Endpoints for managing notes (CRUD operations)")
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Get all notes with pageable sorting")
    public List<NoteResponseDto> getNotes(Pageable pageable) {
        return noteService.getAllNotes(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a note",
            description = """
                    Endpoint for creating a note
                    You should only pass title and content
                    """)
    public NoteResponseDto create(@RequestBody @Valid NoteRequestDto requestDto) {
        return noteService.create(requestDto);
    }

    @PutMapping("/{id}")
    public NoteResponseDto update(
            @PathVariable Long id,
            @RequestBody NoteRequestDto requestDto
    ) {
        return noteService.update(id, requestDto);
    }
}
