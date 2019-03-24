package com.wdong.repository;

import com.wdong.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface GenresRepository extends PagingAndSortingRepository<Genre, Integer> {

    // region api/genre/lookup
    Optional<Genre> findById(Integer integer);

    // endregion
}
