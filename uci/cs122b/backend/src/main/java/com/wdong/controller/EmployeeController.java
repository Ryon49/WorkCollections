package com.wdong.controller;

import com.wdong.model.Employee;
import com.wdong.model.Response;
import com.wdong.model.wrapper.LoginWrapper;
import com.wdong.repository.EmployeesRepository;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @SuppressWarnings("Duplicates")
    @PostMapping(value = "api/employee/login")
    public @ResponseBody
    Response findUser(@RequestParam(name = "email") String email,
                      @RequestParam(name = "password") String password) {

        Employee employee = employeesRepository.findByEmail(email);
        if (employee != null) {
            try {
                boolean success = new StrongPasswordEncryptor().checkPassword(password, employee.getPassword());
                if (!success) {
                    return Response.error("Incorrect password");
                } else {
                    return Response.ok(new LoginWrapper(employee));
                }
            } catch (EncryptionOperationNotPossibleException e) {
                return Response.error("Password unencrypted");
            }
        }
        return Response.error("Username not found");
    }

    private final EmployeesRepository employeesRepository;

    @Autowired
    public EmployeeController(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }
}
