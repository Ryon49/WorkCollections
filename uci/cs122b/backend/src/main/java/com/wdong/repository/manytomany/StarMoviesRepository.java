package com.wdong.repository.manytomany;

import com.wdong.model.manytomany.StarMovie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StarMoviesRepository extends CrudRepository<StarMovie, String> {

    @Query(value = "select sim.movieId from stars s left join stars_in_movies sim on s.id = sim.starId where s.name like CONCAT('%',:name,'%')",
            nativeQuery = true)
    List<String> getMovieIdsByName(@Param("name") String name);
}
