package com.bond.service;

import static java.time.LocalDateTime.now;

import com.bond.dto.CreateNoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.model.Note;
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

    @Override
    public NoteResponseDto create(CreateNoteRequestDto requestDto) {
        Note note = noteMapper.toModel(requestDto);
        note.setCreatedAt(now());
        note.setLastUpdatedAt(now());
        noteRepository.save(note);
        return noteMapper.toResponseDto(note);
    }
}
