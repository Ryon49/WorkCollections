package com.wdong.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wdong.model.Customer;
import com.wdong.model.Employee;

@JsonIgnoreProperties(value = {"success", "errMsg"})
public class LoginWrapper extends ResponseWrapper {
    private Customer customer;
    private Employee employee;

    public LoginWrapper(Customer customer) {
        this.customer = customer;
    }

    public LoginWrapper(Employee employee) {
        this.employee = employee;
    }

    // region getter and setter

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // endregion
}
