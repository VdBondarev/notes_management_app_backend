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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notes controller",
        description = "Endpoints for managing notes")
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Get all notes with pageable sorting")
    public List<NoteResponseDto> getAll(Pageable pageable) {
        return noteService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a note by id")
    public NoteResponseDto getById(@PathVariable Long id) {
        return noteService.getById(id);
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
    @Operation(summary = "Update a note by id",
            description = """
                    Pass as params for updating title or content (or both)
                    """)
    public NoteResponseDto update(
            @PathVariable Long id,
            @RequestBody NoteRequestDto requestDto
    ) {
        return noteService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a note by id")
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search for notes by params",
            description = """
                    You can search by 2 params (title and content)
                    
                    If both of them are null - you will not get any notes
                    
                    If one of them is null - searching will be executed like that: %your_param%
                    
                    If both of them are present
                    Searching will be executed like that: %your_title% and %your_content%
                    """)
    public List<NoteResponseDto> search(@RequestBody NoteRequestDto requestDto, Pageable pageable) {
        return noteService.search(requestDto, pageable);
    }
}
