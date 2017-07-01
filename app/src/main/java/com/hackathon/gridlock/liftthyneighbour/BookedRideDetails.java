package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/*
  Called to display driver details after booking ride for a user using Take Ride
* Created by Mukund Ananthu
* */
public class BookedRideDetails extends Activity {

    private static final String DATE_FORMAT = "yyyyMMddHHmm";

    public static final String DRIVER_NAME = "driverName";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String VEHICLE_NUMBER = "vehicleNumber";
    public static final String DEPARTURE_TIME = "departureTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_ride_details);
        showBookedRideDetails();
    }

    private void showBookedRideDetails() {
        Intent receivedIntent = getIntent();
        if (receivedIntent != null ) {
            TextView driverName = (TextView) findViewById(R.id.tvBRDriverName);
            driverName.setText(receivedIntent.getStringExtra(DRIVER_NAME));
            TextView contactNumber = (TextView) findViewById(R.id.tvBRContactNumber);
            contactNumber.setText(receivedIntent.getStringExtra(CONTACT_NUMBER));
            TextView vehicleNumber = (TextView) findViewById(R.id.tvBRVehicleNumber);
            vehicleNumber.setText(receivedIntent.getStringExtra(VEHICLE_NUMBER));
            TextView pickupTime = (TextView) findViewById(R.id.tvBRPickUpTime);
            pickupTime.setText(getFormattedDateAndTime(receivedIntent.getStringExtra(DEPARTURE_TIME)));
        }
        else {
            Log.e("BookedRideDetails: ", "received intent null");
        }
    }

    private String getFormattedDateAndTime(String ts) {
        Long tsL = Long.parseLong(ts+"000");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date(tsL));
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
