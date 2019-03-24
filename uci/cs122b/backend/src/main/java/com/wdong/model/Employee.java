package com.wdong.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "Employee")
@Table(name = "employees")
public class Employee {
    @Id
    private String email;

    @JsonIgnore
    private String password;

    private String fullname;

    // region getter and setter

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    // endregion
}
