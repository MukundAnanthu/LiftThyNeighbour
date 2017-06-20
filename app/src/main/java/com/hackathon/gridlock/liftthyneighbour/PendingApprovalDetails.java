package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PendingApprovalDetails extends Activity {

    public static final String KEY_USER_ID = "PendingApprovalDetails.USER_ID";
    public static final String KEY_USER_NAME = "PendingApprovalDetails.USER_NAME";
    public static final String KEY_USER_FLAT_NUMBER = "PendingApprovalDetails.FLAT_NUMBER";
    public static final String KEY_VEHICLE_NUMBER = "PendingApprovalDetails.VEHICLE_NUMBER";
    public static final String KEY_CONTACT_NUMBER = "PendingApprovalDetails.CONTACT_NUMBER";
    public static final String KEY_EMAIL = "PendingApprovalDetails.EMAIL";


    private int userId;
    private String userName;
    private String flatNumber;
    private String vehicleNumber;
    private long contactNumber;
    private String email;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_approval_details);

        displayPendingApprovalDetails();
    }

    private void displayPendingApprovalDetails() {
        Intent i = getIntent();
        setUserId(i.getIntExtra(PendingApprovalDetails.KEY_USER_ID, -1));
        setUserName(i.getStringExtra(PendingApprovalDetails.KEY_USER_NAME));
        setFlatNumber(i.getStringExtra(PendingApprovalDetails.KEY_USER_FLAT_NUMBER));
        setVehicleNumber(i.getStringExtra(PendingApprovalDetails.KEY_VEHICLE_NUMBER));
        setContactNumber(i.getIntExtra(PendingApprovalDetails.KEY_CONTACT_NUMBER,-1));
        setEmail(i.getStringExtra(PendingApprovalDetails.KEY_EMAIL));



        TextView tvName = (TextView) findViewById(R.id.tvPAName);
        tvName.setText(getUserName());
        TextView tvFlatNumber = (TextView) findViewById(R.id.tvPAFlatNumber);
        tvFlatNumber.setText(getFlatNumber());
        TextView tvVehicleNumber = (TextView) findViewById(R.id.tvPAVehicleNumber);
        tvVehicleNumber.setText(getVehicleNumber());
        TextView tvContactNumber = (TextView) findViewById(R.id.tvPAContactNumber);
        tvContactNumber.setText(Long.toString(getContactNumber()));
        TextView tvEmail = (TextView) findViewById(R.id.tvPAEmail);
        tvEmail.setText(getEmail());
    }

}