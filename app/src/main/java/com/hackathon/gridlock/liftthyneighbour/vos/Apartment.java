package com.hackathon.gridlock.liftthyneighbour.vos;

/**
 * Apartment VO
 * Created by mukund on 6/20/17.
 */

public class Apartment {

    private int id;
    private String name;


    public Apartment(){}

    public Apartment(int id, String name) {
        this.id = id;
        this.name = name;
    }

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
