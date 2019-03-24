package com.wdong.JpaTest;

import com.wdong.config.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IdTest {

//    @Test
//    public void test() {
//        if (!IdGenerator.initialized) {
//            IdGenerator.init(jdbcTemplate);
//        }
//
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Movie));
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Movie));
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Movie));
//
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Star));
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Star));
//        System.out.println(IdGenerator.getStringId(IdGenerator.type.Star));
//    }
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
}
