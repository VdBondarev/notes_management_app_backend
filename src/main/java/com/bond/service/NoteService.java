package com.bond.service;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NoteService {

    List<NoteResponseDto> getAllNotes(Pageable pageable);

    NoteResponseDto create(NoteRequestDto requestDto);

    NoteResponseDto update(Long id, NoteRequestDto requestDto);

    NoteResponseDto getNoteById(Long id);
}
