package com.wdong.repository;

import com.wdong.model.Star;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StarsRepository extends PagingAndSortingRepository<Star, String> {

    List<Star> findByNameIgnoreCaseContaining(String name);

    @Query(value = "select id from stars where name = :name", nativeQuery = true)
    String getIdByName(@Param("name") String name);

    @Query(value = "select id from stars where name = :name and birthYear = :year", nativeQuery = true)
    String getIdByNameAndBirthYear(@Param("name") String name, @Param("year") int birthYear);
}

