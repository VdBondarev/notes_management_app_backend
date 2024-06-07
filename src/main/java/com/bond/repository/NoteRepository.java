package com.bond.repository;

import com.bond.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    @Query("FROM Note ORDER BY lastUpdatedAt DESC")
    Page<Note> findAllWithDescendingOrderingByLastUpdatedAt(Pageable pageable);
}
