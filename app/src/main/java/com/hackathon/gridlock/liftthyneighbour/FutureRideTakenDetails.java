package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FutureRideTakenDetails extends Activity {

    public static final String EXTRA_TIMESTAMP = "FUTURE_RIDE_TAKEN_DETAILS_TIMESTAMP";
    public static final String EXTRA_DRIVER_NAME = "FUTURE_RIDE_TAKEN_DETAILS_DRIVER_NAME";
    public static final String EXTRA_VEHICLE_NUM = "FUTURE_RIDE_TAKEN_DETAILS_VEHICLE_NUM";
    public static final String EXTRA_CONTACT_NUM = "FUTURE_RIDE_TAKEN_DETAILS_CONTACT_NUM";
    public static final String EXTRA_SRC_NAME = "FUTURE_RIDE_TAKEN_DETAILS_SRC_NAME";
    public static final String EXTRA_DEST_NAME = "FUTURE_RIDE_TAKEN_DETAILS_DEST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_ride_taken_details);

        displayInformation();
    }

    private void displayInformation() {
        Intent receivedIntent = getIntent();

        if (receivedIntent == null) {
            return;
        }

        TextView tvDriverName = (TextView) findViewById(R.id.tvRTDDriverName);
        tvDriverName.setText(receivedIntent.getStringExtra(EXTRA_DRIVER_NAME));

        TextView tvDriverContact = (TextView) findViewById(R.id.tvRTDDriverContact);
        tvDriverContact.setText(receivedIntent.getStringExtra(EXTRA_CONTACT_NUM));

        TextView tvVehicleNum = (TextView) findViewById(R.id.tvRTDVehicleNumber);
        tvVehicleNum.setText(receivedIntent.getStringExtra(EXTRA_VEHICLE_NUM));

        TextView tvSrc = (TextView) findViewById(R.id.tvRTDSourceName);
        tvSrc.setText(receivedIntent.getStringExtra(EXTRA_SRC_NAME));

        TextView tvDest = (TextView) findViewById(R.id.tvRTDDestName);
        tvDest.setText(receivedIntent.getStringExtra(EXTRA_DEST_NAME));

        TextView tvPickupTime = (TextView) findViewById(R.id.tvRTDPickUpTime);
        tvPickupTime.setText(getFormattedDateAndTime(receivedIntent.getStringExtra(EXTRA_TIMESTAMP)));
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
