package com.bond.service;

import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.repository.NoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    public List<NoteResponseDto> getAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable)
                .stream()
                .map(noteMapper::toResponseDto)
                .toList();
    }
}
