package com.wdong.model.simple;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "SimpleSale")
@Table(name = "sales")
public class SimpleSale {

    @Id
    private int id;

    private int customerId;

    private String movieId;

    private int quantity;

    private Date saleDate;

    public SimpleSale() { }

    public SimpleSale(int id, int customerId, String movieId, int quantity, Date saleDate) {
        this.id = id;
        this.customerId = customerId;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
    }

    // region getter and setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
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
