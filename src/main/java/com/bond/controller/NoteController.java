package com.bond.controller;

import com.bond.dto.CreateNoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.service.NoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notes controller",
        description = "Endpoints for managing notes (CRUD operations)")
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    public List<NoteResponseDto> getNotes(Pageable pageable) {
        return noteService.getAllNotes(pageable);
    }

    @PostMapping
    public NoteResponseDto create(@RequestBody CreateNoteRequestDto requestDto) {
        return noteService.create(requestDto);
    }
}
