package com.wdong.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdong.config.IdGenerator;
import com.wdong.model.Response;
import com.wdong.model.request.SaleMovieRequest;
import com.wdong.model.simple.SimpleSale;
import com.wdong.model.wrapper.SalesWrapper;
import com.wdong.repository.simple.SimpleSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/sale")
public class SaleController {

    @PostMapping("checkout")
    public @ResponseBody
    Response checkout(@RequestParam(name = "customerId") int customerId,
                      @RequestParam(name = "movies") String movies,
                      @RequestParam(name = "date") long date) {

        Date now = new Date(date);

        List<SaleMovieRequest> ms = getMovies(movies);
        if (ms == null || ms.size() == 0) {
            return Response.error("Cart has empty items");
        }

        List<Integer> saleIds = new ArrayList<>();

        try {
            Connection conn = master.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(
                    "insert into sales(id, customerId, movieId, saleDate, quantity) values (?,?,?,?,?)");

            for (SaleMovieRequest m : ms) {
                int saleId = IdGenerator.getIntId(IdGenerator.type.Sale);
                stmt.setInt(1, saleId);
                stmt.setInt(2, customerId);
                stmt.setString(3, m.getId());
                stmt.setInt(4, m.getQuantity());
                stmt.setDate(5, now);

                saleIds.add(saleId);
            }

            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SalesWrapper wrapper = new SalesWrapper(saleIds);
        return Response.ok(wrapper);
    }


    // parse movie information from Json format from http post
    private List<SaleMovieRequest> getMovies(@RequestParam(name = "movies") String movies) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(movies, new TypeReference<List<SaleMovieRequest>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    @Qualifier("master_datasource")
    private DataSource master;
}

