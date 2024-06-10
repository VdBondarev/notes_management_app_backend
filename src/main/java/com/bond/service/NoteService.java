package com.bond.service;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NoteService {

    List<NoteResponseDto> getAll(Pageable pageable);

    NoteResponseDto create(NoteRequestDto requestDto);

    NoteResponseDto update(Long id, NoteRequestDto requestDto);

    NoteResponseDto getById(Long id);

    void delete(Long id);

    List<NoteResponseDto> search(String title, String content, Pageable pageable);
}
