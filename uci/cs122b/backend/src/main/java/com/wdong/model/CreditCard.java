package com.wdong.model;

import javax.persistence.*;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "creditcards")
public class CreditCard {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "expiration")
    private Date expiration;

    // region getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    // endregion
}
