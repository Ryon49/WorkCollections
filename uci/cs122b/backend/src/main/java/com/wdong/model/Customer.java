package com.wdong.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private int id;

    private String firstName;

    private String lastName;

    private String address;

    // email is also used as login username
    private String email;

    @JsonIgnore
    private String password;

    private String ccId;

    // region constructor

    // endregion

    // region getter and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public String getCcId() {
        return ccId;
    }

    public void setCcId(String ccId) {
        this.ccId = ccId;
    }

// endregion


}
