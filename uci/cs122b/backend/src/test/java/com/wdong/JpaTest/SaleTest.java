package com.wdong.JpaTest;

import com.wdong.model.Sale;
import com.wdong.repository.SalesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SaleTest {

    @Test
    public void test() {

//        Iterable<Sale> all = salesRepository.findAll();
    }


    @Autowired
    private SalesRepository salesRepository;


}
