package com.wdong.repository;

import com.wdong.model.Movie;
import com.wdong.model.simple.SimpleMovie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;


public interface MoviesRepository extends PagingAndSortingRepository<Movie, String> {
    // region api/movie/top20
    List<Movie> findFirst20ByOrderByRating_RatingDesc();

    Page<Movie> findAllByOrderByRating_RatingDesc(Pageable pageable);
    // endregion

    // region api/movie/info
    @Query(value = "select distinct left(title, 1) as letter from movies order by letter asc", nativeQuery = true)
    List<String> getTitleInfo();

    // endregion

    // region api/movie/letter

    Page<Movie> findAllByTitleStartingWith(String title, Pageable pageable);

    // endregion

    // region api/movie/find
    // by director
    Page<Movie> findByDirectorIgnoreCaseContaining(String title, Pageable pageable);

    // by year
    Page<Movie> findByYearEquals(int year, Pageable pageable);

    // by director and year
    Page<Movie> findByDirectorIgnoreCaseContainingAndYearEquals(String director, int year, Pageable pageable);

    // by movie id and director
    Page<Movie> findByIdInAndDirectorIgnoreCaseContaining(Collection<String> id, String director, Pageable pageable);

    // by movie id
    Page<Movie> findByIdIn(Collection<String> id, Pageable pageable);

    // by movie id and year
    Page<Movie> findByIdInAndYearEquals(Collection<String> id, int year, Pageable pageable);

    // by movie id, director and year
    Page<Movie> findByIdInAndDirectorIgnoreCaseContainingAndYearEquals(Collection<String> id, String director, int year, Pageable pageable);
    // endregion

    // region full text, fuzzy
    @Query(value = "select id from movies where title = :title", nativeQuery = true)
    String getIdByTitle(@Param("title") String title);

    @Query(value = "SELECT * FROM movies WHERE match (title) against (:fullTitle IN BOOLEAN MODE)", nativeQuery = true)
    List<Movie> getFullTextIdByTitle(@Param("fullTitle") String fullTitle);

    @Query(value = "SELECT * FROM movies WHERE match (title) against (:fullTitle IN BOOLEAN MODE) limit :fetchLimit", nativeQuery = true)
    List<Movie> getFullTextIdByTitleWithLimit(@Param("fullTitle") String fullTitle, @Param("fetchLimit") int fetchLimit);

    @Query(value = "select * from movies where edrec(:title, title, :edLimit)", nativeQuery = true)
    List<Movie> getEdMovies(@Param("title") String title, @Param("edLimit") int edLimit);

    @Query(value = "select * from movies where edrec(:title, title, :edLimit) limit :fetchLimit", nativeQuery = true)
    List<Movie> getEdMovies(@Param("title") String title, @Param("edLimit") int edLimit, @Param("fetchLimit") int fetchLimit);
    // endregion

}

