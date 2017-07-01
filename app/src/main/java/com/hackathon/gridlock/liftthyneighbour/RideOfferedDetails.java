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

public class RideOfferedDetails extends Activity {

    public static final String RIDE_EXTRA_TIMESTAMP = "RIDE_TIMESTAMP";
    public static final String RIDE_EXTRA_VEHICLE_NUM = "RIDE_VEHICLE_NUM";
    public static final String RIDE_EXTRA_SOURCE = "RIDE_SOURCE";
    public static final String RIDE_EXTRA_DEST = "RIDE_DEST";
    public static final String RIDE_EXTRA_NUM_PASSENGERS = "RIDE_NUM_PASSENGERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offered_details);

        displayInformation();
    }

    private void displayInformation() {
        Intent receivedIntent = getIntent();

        TextView tvSource = (TextView) findViewById(R.id.tvRODSourceName);
        tvSource.setText(receivedIntent.getStringExtra(RIDE_EXTRA_SOURCE));

        TextView tvDest = (TextView) findViewById(R.id.tvRODDestName);
        tvDest.setText(receivedIntent.getStringExtra(RIDE_EXTRA_DEST));

        TextView tvPickUpTime = (TextView) findViewById(R.id.tvRODPickUpTime);
        tvPickUpTime.setText(getFormattedDateAndTime(receivedIntent.getStringExtra(RIDE_EXTRA_TIMESTAMP)));

        TextView tvNumPassengers = (TextView) findViewById(R.id.tvRODNumPassengers);
        tvNumPassengers.setText( Integer.toString(receivedIntent.getIntExtra(RIDE_EXTRA_NUM_PASSENGERS,0)) );
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
