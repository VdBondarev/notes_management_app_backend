package com.bond.service;

import com.bond.dto.NoteResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NoteService {

    List<NoteResponseDto> getAllNotes(Pageable pageable);
}
