package com.wdong.JpaTest;

import com.wdong.repository.StarsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StarTest {
    @Test
    public void test() {
        String id = starsRepository.getIdByName("Ryon49 V1");
        if (id != null) {
            System.out.println(id);
        } else {
            System.out.println("not found");
        }
    }


    @Autowired
    private StarsRepository starsRepository;
}
