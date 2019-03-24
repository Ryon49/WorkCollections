package com.wdong.JpaTest;

import com.wdong.model.Employee;
import com.wdong.repository.EmployeesRepository;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeTest {

    @Test
    public void test() {
        Iterable<Employee> all = employeesRepository.findAll();
        for (Employee e : all) {
            System.out.println(e.getFullname());
        }
    }

    @Test
    public void test1() {
        Employee employee = employeesRepository.findByEmail("classta@email.edu");

        if (employee != null) {
            System.out.println(employee.getFullname());
        } else {
            System.out.println("No result found");
        }
    }

    @Test
    public void test2() {
        Employee employee = employeesRepository.findByEmail("classta@email.edu");
        boolean success = new StrongPasswordEncryptor().checkPassword("classta", employee.getPassword());

        assert success;
    }

    @Test
    public void test3() {
        Employee employee = employeesRepository.findByEmail("classta@email.edu");
        boolean success = new StrongPasswordEncryptor().checkPassword("classta1", employee.getPassword());

        assert !success;
    }

    @Autowired
    private EmployeesRepository employeesRepository;
}
