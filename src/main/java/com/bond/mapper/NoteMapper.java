package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface NoteMapper {

    NoteResponseDto toResponseDto(Note note);

    Note toModel(NoteRequestDto requestDto);

    Note toUpdatedModel(@MappingTarget Note note, NoteRequestDto requestDto);
}
