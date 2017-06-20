package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TenantDetails extends Activity {


    public static final String KEY_USER_ID = "TenantDetails.USER_ID";
    public static final String KEY_USER_NAME = "TenantDetails.USER_NAME";
    public static final String KEY_USER_FLAT_NUMBER = "TenantDetails.FLAT_NUMBER";
    public static final String KEY_VEHICLE_NUMBER = "TenantDetails.VEHICLE_NUMBER";
    public static final String KEY_CONTACT_NUMBER = "TenantDetails.CONTACT_NUMBER";
    public static final String KEY_EMAIL = "TenantDetails.EMAIL";

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
        setContentView(R.layout.activity_tenant_details);

        displayTenantDetails();
        setUpDeregisterButtonClickListener();
    }

    private void setUpDeregisterButtonClickListener() {
        Button deregisterButton = (Button) findViewById(R.id.bDeRegisterTenant);
        deregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deregisterTenant();
            }
        });
    }


    private void deregisterTenant() {
        //TODO make API call to delete tenant display toast for result
    }

    private void displayTenantDetails() {
        Intent i = getIntent();
        setUserId(i.getIntExtra(TenantDetails.KEY_USER_ID, -1));
        setUserName(i.getStringExtra(TenantDetails.KEY_USER_NAME));
        setFlatNumber(i.getStringExtra(TenantDetails.KEY_USER_FLAT_NUMBER));
        setVehicleNumber(i.getStringExtra(TenantDetails.KEY_VEHICLE_NUMBER));
        setContactNumber(i.getIntExtra(TenantDetails.KEY_CONTACT_NUMBER,-1));
        setEmail(i.getStringExtra(TenantDetails.KEY_EMAIL));



        TextView tvName = (TextView) findViewById(R.id.tvTDName);
        tvName.setText(getUserName());
        TextView tvFlatNumber = (TextView) findViewById(R.id.tvTDFlatNumber);
        tvFlatNumber.setText(getFlatNumber());
        TextView tvVehicleNumber = (TextView) findViewById(R.id.tvTDVehicleNumber);
        tvVehicleNumber.setText(getVehicleNumber());
        TextView tvContactNumber = (TextView) findViewById(R.id.tvTDContactNumber);
        tvContactNumber.setText(Long.toString(getContactNumber()));
        TextView tvEmail = (TextView) findViewById(R.id.tvTDEmail);
        tvEmail.setText(getEmail());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aAHlogout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        purgeCredentials();
        redirectToSignInPage();
    }

    private void purgeCredentials() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.KEY_TOKEN), "");
        editor.putString(getString(R.string.KEY_USER_TYPE),"");
        editor.putBoolean(getString(R.string.KEY_IS_USER_ADMIN),false);
        editor.putBoolean(getString(R.string.KEY_IS_USER_SIGNED),false);
        editor.commit();
    }

    private void redirectToSignInPage() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
