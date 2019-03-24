package com.wdong.JpaTest;

import com.wdong.model.SimpleGenre;
import com.wdong.model.manytomany.GenreMovie;
import com.wdong.repository.simple.SimpleGenresRepository;
import com.wdong.repository.manytomany.GenreMoviesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreTest {
    @Autowired
    private SimpleGenresRepository simpleGenresRepository;

    @Autowired
    private GenreMoviesRepository genreMoviesRepository;

    @Test
    public void test1() {
        Iterable<SimpleGenre> info = simpleGenresRepository.findAll();

    }

    @Test
    public void test2() {
//        Request.setSort(6);
        Page<GenreMovie> gms = genreMoviesRepository.findAllById_GenreId(1,
                PageRequest.of(0, 20, Sort.by(Sort.Order.asc("id_movie_title"))));

    }
}
