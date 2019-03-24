package com.wdong.JpaTest;

import com.wdong.model.manytomany.StarMovie;
import com.wdong.repository.manytomany.StarMoviesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StarMovies {

    @Autowired
    private StarMoviesRepository starMoviesRepository;

    @Test
    public void test1() {
        List<String> c = starMoviesRepository.getMovieIdsByName("c");
        System.out.println(c.size());
    }

    private void print(List<StarMovie> cs) {
        for (StarMovie c : cs) {
            System.out.println(c.getId().getMovieId());
        }
    }
}
