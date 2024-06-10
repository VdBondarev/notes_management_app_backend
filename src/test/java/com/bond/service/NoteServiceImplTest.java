package com.bond.service;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.mapper.NoteMapper;
import com.bond.model.Note;
import com.bond.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
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
            Verify that getAll() method works as expected
            """)
    public void getAll_ValidPageable_ReturnsValidList() {
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

        when(noteRepository.findAll(pageable))
                .thenReturn(page);
        when(noteMapper.toResponseDto(firstExpectedNote)).thenReturn(firstExpectedDto);
        when(noteMapper.toResponseDto(secondExpectedNote)).thenReturn(secondExpectedDto);

        List<NoteResponseDto> expectedList = List.of(firstExpectedDto, secondExpectedDto);
        List<NoteResponseDto> actualList = noteService.getAll(pageable);

        assertEquals(expectedList, actualList);

        verify(noteRepository, times(1))
                .findAll(pageable);
        verify(noteMapper, times(1)).toResponseDto(firstExpectedNote);
        verify(noteMapper, times(1)).toResponseDto(secondExpectedNote);
        verifyNoMoreInteractions(noteMapper);
    }

    @Test
    @DisplayName("""
            Verify that create() method works as expected
            """)
    public void create_ValidRequestDto_ReturnsValidResponse() {
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

    @Test
    @DisplayName("""
            Verify that update() method works as expected with non-valid params
            """)
    public void update_NonValidParams_ThrowsException() {
        // valid id but non-valid request dto, expect IllegalArgumentException
        NoteRequestDto requestDto = new NoteRequestDto(null, null);
        Long validId = 1L;

        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class, () -> noteService.update(validId, requestDto)
        );

        String expectedMessage = """
                Both title and content cannot be empty
                Update at least one of them
                """;
        String actualMessage = illegalArgumentException.getMessage();

        assertEquals(expectedMessage, actualMessage);

        // valud requestDto, but not valid id, expect EntityNotFoundException
        NoteRequestDto newRequestDto = new NoteRequestDto("Test title", "Test content");
        Long nonValidId = -10L;

        when(noteRepository.findById(nonValidId)).thenReturn(Optional.empty());
        EntityNotFoundException notFoundException = assertThrows(
                EntityNotFoundException.class, () -> noteService.update(nonValidId, newRequestDto)
        );

        expectedMessage = "Can't find a note with id " + nonValidId;
        actualMessage = notFoundException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("""
            Verify that update() method works as expected with valid params
            """)
    public void update_ValidNote_ReturnsValidNote() {
        NoteRequestDto requestDto = new NoteRequestDto("New title", "New content");

        Long id = 1L;

        Note expectedNote = new Note()
                .setId(id)
                .setTitle("Old title")
                .setContent("Old content")
                .setCreatedAt(now())
                .setLastUpdatedAt(now().minusDays(1));

        when(noteRepository.findById(id)).thenReturn(Optional.of(expectedNote));

        expectedNote
                .setContent(requestDto.content())
                .setTitle(requestDto.title());

        NoteResponseDto expectedResponseDto = createResponseDtoFromModel(expectedNote);

        when(noteMapper.toUpdatedModel(expectedNote, requestDto)).thenReturn(expectedNote);
        when(noteRepository.save(expectedNote)).thenReturn(expectedNote);
        when(noteMapper.toResponseDto(expectedNote)).thenReturn(expectedResponseDto);

        NoteResponseDto actualResponseDto = noteService.update(id, requestDto);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("""
            Verify that getById() method works as expected with validId
            """)
    public void getById_ValidId_ReturnsValidResponseDto() {
        Long id = 1L;

        Note expectedNote = new Note()
                .setId(id)
                .setTitle("Test title")
                .setContent("Test content")
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        NoteResponseDto expectedResponseDto = createResponseDtoFromModel(expectedNote);

        when(noteRepository.findById(id)).thenReturn(Optional.of(expectedNote));
        when(noteMapper.toResponseDto(expectedNote)).thenReturn(expectedResponseDto);

        NoteResponseDto actualResponseDto = noteService.getById(id);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("""
            Verify that getById() method works as expected with non-valid params
            """)
    public void getById_NonValidParams_ThrowsException() {
        Long id = -10L;

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> noteService.getById(id)
        );

        String expectedMessage = "Can't find a note with id " + id;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("""
            Verify that search() method works as expected with valid params
            """)
    public void search_ValidParams_ReturnsValidList() {
        Note firstExpectedNote = new Note()
                .setId(1L)
                .setTitle("First test title")
                .setContent("First test content")
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        Note secondExpectedNote = new Note()
                .setId(2L)
                .setTitle("Second test title")
                .setContent("Second test content")
                .setCreatedAt(now())
                .setLastUpdatedAt(now());

        List<Note> expectedNoteList;
        expectedNoteList = List.of(firstExpectedNote, secondExpectedNote);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Note> page = new PageImpl<>(expectedNoteList, pageable, expectedNoteList.size());

        NoteResponseDto firstResponseDto = createResponseDtoFromModel(firstExpectedNote);
        NoteResponseDto secondResponseDto = createResponseDtoFromModel(secondExpectedNote);

        when(noteRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(page);
        when(noteMapper.toResponseDto(firstExpectedNote)).thenReturn(firstResponseDto);
        when(noteMapper.toResponseDto(secondExpectedNote)).thenReturn(secondResponseDto);

        String title = "test";
        String content = "test";

        List<NoteResponseDto> actualList = noteService.search(title, content, pageable);
        List<NoteResponseDto> expectedList = List.of(firstResponseDto, secondResponseDto);

        assertEquals(expectedList, actualList);

        verify(noteMapper, times(2)).toResponseDto(any());
        verifyNoMoreInteractions(noteMapper);
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
