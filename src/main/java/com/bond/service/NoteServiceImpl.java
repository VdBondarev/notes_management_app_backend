package com.bond.service;

import static java.time.LocalDateTime.now;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.model.Note;
import com.bond.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private static final String ID_FIELD = "id";
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String LAST_UPDATED_AT_FIELD = "lastUpdatedAt";
    private static final String TITLE_FIELD = "title";
    private static final String CONTENT_FIELD = "content";
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    public List<NoteResponseDto> getAll(Pageable pageable) {
        return noteRepository.findAllWithDescendingOrderingByLastUpdatedAt(pageable)
                .stream()
                .map(noteMapper::toResponseDto)
                .toList();
    }

    @Override
    public NoteResponseDto create(NoteRequestDto requestDto) {
        Note note = noteMapper.toModel(requestDto)
                .setCreatedAt(now())
                .setLastUpdatedAt(now());
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
                        "Can't find a note with id " + id
                ));
        note = noteMapper.toUpdatedModel(note, requestDto)
                .setLastUpdatedAt(now());
        noteRepository.save(note);
        return noteMapper.toResponseDto(note);
    }

    @Override
    public NoteResponseDto getById(Long id) {
        return noteRepository.findById(id)
                .map(noteMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a note with id " + id
                ));
    }

    @Override
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    @Override
    public List<NoteResponseDto> search(String title, String content, Pageable pageable) {
        if ((title == null && content == null)
                || (Objects.requireNonNull(title).isEmpty()
                && Objects.requireNonNull(content).isEmpty())
        ) {
            throw new IllegalArgumentException("Searching should be done by at least 1 param");
        }
        ExampleMatcher exampleMatcher = createExampleMatcher();
        Example<Note> example = Example.of(
                getNoteFromSearchParams(title, content),
                exampleMatcher
        );
        return noteRepository.findAll(example, pageable)
                .stream()
                .map(noteMapper::toResponseDto)
                .toList();
    }

    private Note getNoteFromSearchParams(String title, String content) {
        return new Note()
                .setTitle(title)
                .setContent(content);
    }

    private ExampleMatcher createExampleMatcher() {
        return ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths(ID_FIELD, CREATED_AT_FIELD, LAST_UPDATED_AT_FIELD)
                .withMatcher(
                        TITLE_FIELD,
                        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                )
                .withMatcher(
                        CONTENT_FIELD,
                        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                );
    }
}
