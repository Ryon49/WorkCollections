package com.wdong.repository;

import com.wdong.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeesRepository extends CrudRepository<Employee, String> {
    boolean existsByEmail(String email);

    Employee findByEmail(String email);
}
