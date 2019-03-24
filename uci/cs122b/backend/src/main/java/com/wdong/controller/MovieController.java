package com.wdong.controller;

import com.wdong.cache.StarMovieCache;
import com.wdong.config.GenreGenerator;
import com.wdong.config.IdGenerator;
import com.wdong.config.ReportConfig;
import com.wdong.config.Request;
import com.wdong.model.Movie;
import com.wdong.model.Response;
import com.wdong.model.manytomany.GenreMovie;
import com.wdong.model.wrapper.MovieHeadWrapper;
import com.wdong.model.wrapper.IdsWrapper;
import com.wdong.model.wrapper.MoviesWrapper;
import com.wdong.repository.GenresRepository;
import com.wdong.repository.MoviesRepository;
import com.wdong.repository.StarsRepository;
import com.wdong.repository.manytomany.GenreMoviesRepository;
import com.wdong.repository.manytomany.StarMoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/movie")
public class MovieController {
    // region api
    @GetMapping("lookup/{id}")
    public @ResponseBody
    Movie getMovieById(@PathVariable(name = "id") String id) {
        return moviesRepository.findById(id).get();
    }

    @PostMapping(value = "searchTitle")
    public @ResponseBody
    Response searchTitle(@RequestParam(name = "title") String title) {
        String fullTextQuery = getFullTextQuery(title);
        List<Movie> fulltextMovies = moviesRepository.getFullTextIdByTitleWithLimit(fullTextQuery, 10);

        int size = Request.getSize() - fulltextMovies.size();
        if (size > 0) {
            fulltextMovies = appendEds(fulltextMovies, title, size);
        }

        return Response.ok(new MovieHeadWrapper(
                fulltextMovies.stream().map(movie -> new MovieHeadWrapper.MovieHead(movie.getId(), movie.getTitle()))
                        .collect(Collectors.toList())));
    }

    @RequestMapping(value = "find", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody
    Response find(
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "year") Optional<Integer> year,
            @RequestParam(value = "director") Optional<String> director,
            @RequestParam(value = "star") Optional<String> star,
            @RequestParam(value = "pageNum") Optional<Integer> pageNum,
            @RequestParam(value = "maxRecords") Optional<Integer> maxRecords,
            @RequestParam(value = "sortBy") Optional<Integer> sortBy) {

        long tsTimer = System.nanoTime();

        Request.handlePageAndSort(pageNum, maxRecords, sortBy);
        PageRequest request = Request.getRequest();
        Page<Movie> movies;

        StringJoiner joiner = new StringJoiner(", ");

        long tjTimer = System.nanoTime();
        List<String> fulltextIds = null;
        if (title.isPresent()) {
            String query = getFullTextQuery(title.get());
            List<Movie> fullTextMovies = appendEds(moviesRepository.getFullTextIdByTitle(query), title.get(), -1);

            fulltextIds = fullTextMovies.stream()
                    .map(Movie::getId).collect(Collectors.toList());
        }

        if (star.isPresent()) {
            List<String> ids;
            String name = star.get();

            if (StarMovieCache.hit(name)) {
                ids = StarMovieCache.getCache();
            } else {
                ids = starMoviesRepository.getMovieIdsByName(name);
                StarMovieCache.cache(name, ids);
            }

            if (title.isPresent()) {
                ids = ids.stream()
                        .filter(fulltextIds::contains)
                        .collect(Collectors.toList());
                joiner.add("Full Title: '" + title.get() + "'");
            }

            if (year.isPresent()) {
                if (director.isPresent()) {
                    movies = moviesRepository.findByIdInAndDirectorIgnoreCaseContainingAndYearEquals(
                            ids, director.get(), year.get(), request);
                    joiner.add("Director: '" + director.get() + "'");
                } else {
                    movies = moviesRepository.findByIdInAndYearEquals(ids, year.get(), request);
                }
                joiner.add("Year: '" + year.get() + "'");
            } else {
                if (director.isPresent()) {
                    movies = moviesRepository.findByIdInAndDirectorIgnoreCaseContaining(
                            ids, director.get(), request);
                    joiner.add("Director: '" + director.get() + "'");
                } else {
                    movies = moviesRepository.findByIdIn(ids, request);
                }
            }
            joiner.add("Star: '" + name + "'");
        } else {
            if (year.isPresent()) {
                if (title.isPresent() && director.isPresent()) {
                    movies = moviesRepository.findByIdInAndDirectorIgnoreCaseContainingAndYearEquals(fulltextIds,
                            director.get(), year.get(), request);
                    joiner.add("Full Title: '" + title.get() + "'");
                    joiner.add("Director: '" + director.get() + "'");
                } else if (title.isPresent()) {
                    movies = moviesRepository.findByIdInAndYearEquals(
                            fulltextIds, year.get(), request);
                    joiner.add("Full Title: '" + title.get() + "'");
                } else if (director.isPresent()) {
                    movies = moviesRepository.findByDirectorIgnoreCaseContainingAndYearEquals(
                            director.get(), year.get(), request);
                    joiner.add("Director: '" + director.get() + "'");
                } else {
                    movies = moviesRepository.findByYearEquals(year.get(), request);
                }
                joiner.add("Year: '" + year.get() + "'");
            } else {
                if (title.isPresent() && director.isPresent()) {
                    movies = moviesRepository.findByIdInAndDirectorIgnoreCaseContaining(
                            fulltextIds, director.get(), request);
                    joiner.add("Full Title: '" + title.get() + "'");
                    joiner.add("Director: '" + director.get() + "'");
                } else if (title.isPresent()) {
                    movies = moviesRepository.findByIdIn(
                            fulltextIds, request);
                    joiner.add("Full Title: '" + title.get() + "'");
                } else if (director.isPresent()) {
                    movies = moviesRepository.findByDirectorIgnoreCaseContaining(
                            director.get(), request);
                    joiner.add("Director: '" + director.get() + "'");
                } else {
                    movies = moviesRepository.findAll(request);
                    joiner.add("Default Search");
                }
            }
        }
        long tj = System.nanoTime() - tjTimer;

        MoviesWrapper wrapper = new MoviesWrapper(movies.iterator(), movies.getNumber(), movies.getTotalPages(), movies.getSize(), sortBy.orElse(0));
        wrapper.setSearchDescription(joiner.toString());

        long ts = System.nanoTime() - tsTimer;

        if (ReportConfig.isStart()) {
            ReportConfig.addRecord(ts, tj);
        }

        return Response.ok(wrapper);
    }

    @GetMapping(value = "top20")
    public @ResponseBody
    Response top20(@RequestParam(value = "pageNum") Optional<Integer> pageNum,
                   @RequestParam(value = "maxRecords") Optional<Integer> maxRecords,
                   @RequestParam(value = "sortBy") Optional<Integer> sortBy) {

        Request.handlePageAndSort(pageNum, maxRecords, sortBy);

        PageRequest request = Request.getRequest();
        Page<Movie> movies = moviesRepository.findAllByOrderByRating_RatingDesc(request);

        MoviesWrapper wrapper = new MoviesWrapper(movies.iterator(), movies.getNumber(), movies.getTotalPages(), movies.getSize(), sortBy.orElse(0));
        return Response.ok(wrapper);
    }

    @GetMapping(value = "letter")
    public @ResponseBody
    Response findByLetter(@RequestParam(value = "letter") Optional<String> letter,
                          @RequestParam(value = "pageNum") Optional<Integer> pageNum,
                          @RequestParam(value = "maxRecords") Optional<Integer> maxRecords,
                          @RequestParam(value = "sortBy") Optional<Integer> sortBy) {
        Request.handlePageAndSort(pageNum, maxRecords, sortBy);
        PageRequest request = Request.getRequest();

        Page<Movie> movies = moviesRepository.findAllByTitleStartingWith(letter.get(), request);

        MoviesWrapper wrapper = new MoviesWrapper(movies.iterator(), movies.getNumber(), movies.getTotalPages(), movies.getSize(), sortBy.orElse(0));

        wrapper.setSearchDescription("Title start with: '" + letter.get() + "'");
        return Response.ok(wrapper);
    }

    @GetMapping(value = "genre")
    public @ResponseBody
    Response findByGenre(@RequestParam(value = "genreId") Optional<Integer> genre,
                         @RequestParam(value = "pageNum") Optional<Integer> pageNum,
                         @RequestParam(value = "maxRecords") Optional<Integer> maxRecords,
                         @RequestParam(value = "sortBy") Optional<Integer> sortBy) {
        Request.handlePageAndSort(pageNum, maxRecords, sortBy, true);
        PageRequest request = Request.getRequest();

        Page<GenreMovie> gms = genreMoviesRepository.findAllById_GenreId(genre.get(), request);
        Page<Movie> movies = gms.map(gm -> {
            return gm.getId().getMovie();
        });

        MoviesWrapper wrapper = new MoviesWrapper(movies.iterator(), movies.getNumber(), movies.getTotalPages(), movies.getSize(), sortBy.orElse(0));
        wrapper.setSearchDescription("Genre: '" + genresRepository.findById(genre.get()).get().getName() + "'");
        return Response.ok(wrapper);
    }

    @PostMapping(value = "add")
    public @ResponseBody
    Response add(@RequestParam(name = "titles") String[] titles,
                 @RequestParam(name = "years") int[] years,
                 @RequestParam(name = "directors") String[] directors,
                 @RequestParam(name = "genres") String[] genres,
                 @RequestParam(name = "starNames") String[] starNames,
                 @RequestParam(name = "starYears") int[] starYears) {
        HashMap<String, String> ids = new HashMap<>();
        boolean hasNewRecord = false;
        try {
            Connection conn = master.getConnection();
            conn.setAutoCommit(false);
            CallableStatement stmt = conn.prepareCall("call add_movie(?,?,?,?,?,?,?,?)");

            for (int i = 0; i < titles.length; i++) {
                String id = moviesRepository.getIdByTitle(titles[i]);
                if (id == null) {
                    hasNewRecord = true;
                    String movieId = IdGenerator.getStringId(IdGenerator.type.Movie);
                    stmt.setString(1, movieId);
                    stmt.setString(2, titles[i]);
                    stmt.setInt(3, years[i]);
                    stmt.setString(4, directors[i]);
                    stmt.setInt(5, GenreGenerator.get(genres[i]));
                    String starId = starsRepository.getIdByName(starNames[i]);
                    if (starId != null) {
                        stmt.setString(6, starId);
                        stmt.setString(7, null);
                        stmt.setString(8, null);
                    } else {
                        stmt.setString(6, IdGenerator.getStringId(IdGenerator.type.Star));
                        stmt.setString(7, starNames[i]);
                        stmt.setInt(8, starYears[i]);
                    }
                    ids.put(movieId, titles[i]);
                    stmt.addBatch();
                } else {
                    ids.put(id, titles[i]);
                }
            }

            if (hasNewRecord) {
                if (GenreGenerator.hasNewGenre()) {
                    PreparedStatement newGenreStmt = conn.prepareStatement("insert into genres(id, name) values (?, ?)");
                    for (Map.Entry<String, Integer> entry : GenreGenerator.getNewGenreMapping().entrySet()) {
                        newGenreStmt.setInt(1, entry.getValue());
                        newGenreStmt.setString(2, entry.getKey());
                        newGenreStmt.addBatch();
                    }
                    newGenreStmt.executeBatch();
                    newGenreStmt.close();
                }
                stmt.executeBatch();
                conn.commit();
                stmt.close();
            }
            conn.setAutoCommit(true);
            conn.close();
            return Response.ok(new IdsWrapper(ids));
        } catch (SQLException e) {
            return Response.error("A network error?");
        }
    }
    // endregion

    private List<Movie> appendEds(List<Movie> movies, String title, int size) {
        ArrayList<Movie> newMovies = new ArrayList<>(movies);
        List<String> ids = newMovies.stream().map(Movie::getId).collect(Collectors.toList());
        List<Movie> edMovies;
        if (size >= 0) {
            edMovies = moviesRepository.getEdMovies(title, getEditDistance(title), size * 2);
        } else {
            edMovies = moviesRepository.getEdMovies(title, getEditDistance(title));
        }
        int cnt = 0;
        for (Movie m : edMovies) {
            if (cnt == size) {
                break;
            }
            if (!ids.contains(m.getId())) {
                newMovies.add(m);
                cnt++;
            }
        }
        return newMovies;
    }

    private String getFullTextQuery(String title) {
        String[] split = title.split(" ");
        String query;
        if (split.length == 1) {

            query = String.format("'+%s*'", title);
        } else {
            StringJoiner joiner = new StringJoiner(" ", "'", "' ");
            for (String s : split) {
                joiner.add(String.format("+%s*", s));
            }
            query = joiner.toString();
        }
        return query;
    }

    private int getEditDistance(String title) {
        if (title.length() < 2) {
            return 0;
        }
        return (int) Math.ceil((title.length() - 2) / 3.0f);
    }

    private final MoviesRepository moviesRepository;

    private final StarsRepository starsRepository;

    private final GenreMoviesRepository genreMoviesRepository;

    private final StarMoviesRepository starMoviesRepository;

    private final GenresRepository genresRepository;

    @Autowired
    @Qualifier("master_datasource")
    private DataSource master;

    @Autowired
    public MovieController(MoviesRepository moviesRepository, GenreMoviesRepository genreMoviesRepository, StarMoviesRepository starMoviesRepository, GenresRepository genresRepository, StarsRepository starsRepository) {
        this.moviesRepository = moviesRepository;
        this.genreMoviesRepository = genreMoviesRepository;
        this.starMoviesRepository = starMoviesRepository;
        this.genresRepository = genresRepository;
        this.starsRepository = starsRepository;
    }
}
