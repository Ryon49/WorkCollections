package com.wdong.controller;

import com.wdong.config.GenreGenerator;
import com.wdong.config.IdGenerator;
import com.wdong.model.Response;
import com.wdong.model.Star;
import com.wdong.model.wrapper.IdsWrapper;
import com.wdong.repository.StarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

@RestController
@RequestMapping(value = "api/star")
public class StarController {

    @GetMapping("lookup/{id}")
    public @ResponseBody Star getStarById(@PathVariable(name = "id") String id) {
        return starsRepository.findById(id).get();
    }

    @PostMapping("add")
    public @ResponseBody
    Response add(@RequestParam(name = "names") String[] names,
                 @RequestParam(name = "birthYears") int[] years) {
        try {
            Connection conn = master.getConnection();
            HashMap<String, String> ids = new HashMap<>();
            boolean hasNewRecord = false;

            PreparedStatement stmt = conn.prepareStatement("insert into stars(id, name, birthYear) values (?, ?, ?)");
            for (int i = 0; i < names.length; i++) {
                String id = starsRepository.getIdByName(names[i]);
                if (id == null) {
                    hasNewRecord = true;
                    String newId = IdGenerator.getStringId(IdGenerator.type.Star);
                    stmt.setString(1, newId);
                    stmt.setString(2, names[i]);
                    stmt.setInt(3, years[i]);
                    stmt.addBatch();
                    ids.put(newId, names[i]);
                } else {
                    ids.put(id, names[i]);
                }
            }

            if (hasNewRecord) {
                conn.setAutoCommit(false);
                stmt.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            }
            return Response.ok(new IdsWrapper(ids));
        } catch (Exception e) {
            return Response.error("A database error?");
        }
    }

    @Autowired
    @Qualifier("master_datasource")
    private DataSource master;

    private final StarsRepository starsRepository;

    @Autowired
    public StarController(StarsRepository starsRepository) {
        this.starsRepository = starsRepository;
    }
}
