package com.wdong.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name = "SimpleGenres")
@Table(name = "genres")
public class SimpleGenre {
    // region properties
    @Id
    private int id;

    private String name;
    // endregion

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
