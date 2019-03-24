package com.wdong.repository.manytomany;

import com.wdong.model.manytomany.GenreMovie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreMoviesRepository extends PagingAndSortingRepository<GenreMovie, Integer> {
    Page<GenreMovie> findAllById_GenreId(int genreId, Pageable pageable);
}
