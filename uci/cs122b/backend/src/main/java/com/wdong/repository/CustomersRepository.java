package com.wdong.repository;

import com.wdong.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomersRepository extends CrudRepository<Customer, Integer> {

    boolean existsByEmail(String email);

    Optional<Customer> findByEmailAndPassword(String email, String password);

    Customer findByEmail(String email);
}
