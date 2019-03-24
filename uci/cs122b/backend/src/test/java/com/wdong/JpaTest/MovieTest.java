package com.wdong.JpaTest;

import com.wdong.config.Request;
import com.wdong.model.Movie;
import com.wdong.model.Star;
import com.wdong.model.simple.SimpleMovie;
import com.wdong.model.wrapper.MovieHeadWrapper;
import com.wdong.repository.MoviesRepository;
import com.wdong.repository.StarsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MovieTest {

    private PageRequest request = Request.getRequest();
    @Autowired
    private StarsRepository starsRepository;

    @Autowired
    private MoviesRepository moviesRepository;

    @Test
    public void test() {
        List<Star> stars = starsRepository.findByNameIgnoreCaseContaining("Fred");

        stars.stream().map(Star::getMovies)
                .flatMap(Collection::stream)
                .map(Movie::getId)
                .distinct()
                .limit(Request.getSize())
                .collect(Collectors.toList());

    }

    @Test
    public void test3() {
        List<String> letters = moviesRepository.getTitleInfo().stream()
                .filter(c -> Character.isLetterOrDigit(c.toCharArray()[0])).collect(Collectors.toList());
        for (String c : letters) {
            System.out.println(c);
        }
    }

    @Test
    public void test4() {
        Page<Movie> a = moviesRepository.findAllByTitleStartingWith("[", request);
    }
}
