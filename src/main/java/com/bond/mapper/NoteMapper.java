package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.CreateNoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.model.Note;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface NoteMapper {

    NoteResponseDto toResponseDto(Note note);

    Note toModel(CreateNoteRequestDto requestDto);
}
