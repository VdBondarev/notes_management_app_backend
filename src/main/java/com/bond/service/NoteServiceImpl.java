package com.bond.service;

import static java.time.LocalDateTime.now;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.model.Note;
import com.bond.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public NoteResponseDto create(NoteRequestDto requestDto) {
        Note note = noteMapper.toModel(requestDto);
        note.setCreatedAt(now());
        note.setLastUpdatedAt(now());
        noteRepository.save(note);
        return noteMapper.toResponseDto(note);
    }

    @Override
    public NoteResponseDto update(Long id, NoteRequestDto requestDto) {
        if ((requestDto.title() == null || requestDto.title().isEmpty())
                && (requestDto.content() == null || requestDto.content().isEmpty())) {
            throw new IllegalArgumentException("""
                    Both title and content cannot be empty
                    Update at least one of them
                    """);
        }
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a note with id " + id)
                );
        note = noteMapper.toUpdatedModel(note, requestDto);
        note.setLastUpdatedAt(now());
        noteRepository.save(note);
        return noteMapper.toResponseDto(note);
    }
}
