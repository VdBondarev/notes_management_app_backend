package com.bond.service;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.model.Note;
import com.bond.repository.NoteRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {
    @InjectMocks
    private NoteServiceImpl noteService;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private NoteMapper noteMapper;

    @Test
    @DisplayName("""
            Verify that getAllNotes() method works as expected
            """)
    public void getAllNotes_ValidPageable_ReturnsValidList() {
        Note firstExpectedNote = new Note()
                .setId(1L)
                .setTitle("First note")
                .setContent("First note")
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        Note secondExpectedNote = new Note()
                .setId(2L)
                .setTitle("Second note")
                .setContent("Second note")
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        List<Note> noteList = List.of(firstExpectedNote, secondExpectedNote);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Note> page = new PageImpl<>(noteList, pageable, noteList.size());

        NoteResponseDto firstExpectedDto = createResponseDtoFromModel(firstExpectedNote);
        NoteResponseDto secondExpectedDto = createResponseDtoFromModel(secondExpectedNote);

        when(noteRepository.findAll(pageable)).thenReturn(page);
        when(noteMapper.toResponseDto(firstExpectedNote)).thenReturn(firstExpectedDto);
        when(noteMapper.toResponseDto(secondExpectedNote)).thenReturn(secondExpectedDto);

        List<NoteResponseDto> expectedList = List.of(firstExpectedDto, secondExpectedDto);
        List<NoteResponseDto> actualList = noteService.getAllNotes(pageable);

        assertEquals(expectedList, actualList);

        verify(noteRepository, times(1)).findAll(pageable);
        verify(noteMapper, times(1)).toResponseDto(firstExpectedNote);
        verify(noteMapper, times(1)).toResponseDto(secondExpectedNote);
        verifyNoMoreInteractions(noteMapper);
    }

    @Test
    @DisplayName("""
            Verify that create() method works as expected
            """)
    public void create_ValidNote_ReturnsValidNote() {
        NoteRequestDto requestDto = new NoteRequestDto("Test title", "Test content");

        Note expectedNote = new Note()
                .setId(1L)
                .setTitle(requestDto.title())
                .setContent(requestDto.content())
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        NoteResponseDto expectedResponseDto = createResponseDtoFromModel(expectedNote);

        when(noteMapper.toModel(requestDto)).thenReturn(expectedNote);
        when(noteRepository.save(expectedNote)).thenReturn(expectedNote);
        when(noteMapper.toResponseDto(expectedNote)).thenReturn(expectedResponseDto);

        NoteResponseDto actualResponseDto = noteService.create(requestDto);

        assertEquals(expectedResponseDto, actualResponseDto);

        verify(noteRepository, times(1)).save(expectedNote);
        verify(noteMapper, times(1)).toModel(requestDto);
        verify(noteMapper, times(1)).toResponseDto(expectedNote);
        verifyNoMoreInteractions(noteMapper, noteRepository);
    }

    private NoteResponseDto createResponseDtoFromModel(Note note) {
        return new NoteResponseDto(
                note.getId(),
                note.getTitle(),
                note.getTitle(),
                note.getCreatedAt(),
                note.getLastUpdatedAt()
        );
    }
}
