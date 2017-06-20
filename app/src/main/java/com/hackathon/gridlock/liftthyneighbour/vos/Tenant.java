package com.hackathon.gridlock.liftthyneighbour.vos;

/**
 * Created by mukund on 6/20/17.
 */

public class Tenant {

    private int userId;
    private String userName;
    private String flatNumber;
    private String vehicleNumber;
    private long contactNumber;
    private String email;


    public Tenant() {}

    public Tenant(int userId, String userName, String flatNumber, String vehicleNumber, long contactNumber, String email) {
        this.userId = userId;
        this.userName = userName;
        this.flatNumber = flatNumber;
        this.vehicleNumber = vehicleNumber;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
