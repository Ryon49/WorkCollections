package com.wdong.model;


import javax.persistence.*;
import java.sql.Date;

@Entity(name = "sales")
@Table(name = "sales")
public class Sale {

    @Id
    private int id;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "saleDate")
    private Date saleDate;

    // region relations
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId", referencedColumnName = "id")
    private Movie movie;
    // endregion

    // region constructor

    // endregion

    // region getter and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // endregion
}
