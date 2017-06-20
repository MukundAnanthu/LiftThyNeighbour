package com.hackathon.gridlock.liftthyneighbour.vos;

/**
 * Created by mukund on 6/20/17.
 */

public class TechPark {

    private int id;
    private String name;


    public TechPark(){}

    public TechPark(int id, String name) {
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
