package com.wdong.model.simple;

public class SimpleStar {

    private String id;

    private String name;

    private int birthYear;

    public SimpleStar() {
    }

    public SimpleStar(String id) {
        this.id = id;
    }


    // region getter and setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public void setBirthYear(String birthYear) {
        try {
            setBirthYear(Integer.parseInt(birthYear));
        } catch (Exception e) {
            System.out.println(name + " has no birth year");
        }
    }
    // endregion
}
