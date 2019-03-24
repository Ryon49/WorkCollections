package com.wdong.JpaTest;

import com.wdong.model.Customer;
import com.wdong.repository.CustomersRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerTest {

    @Autowired
    private CustomersRepository customersRepository;

//    @Test
//    public void findUser() {
//        String email = "a@email.com";
//        String password = "a2";
//
//        Optional<Customer> user = customersRepository.findByEmailAndPassword(email, password);
//
//        assert (user.isPresent());
//    }
}
