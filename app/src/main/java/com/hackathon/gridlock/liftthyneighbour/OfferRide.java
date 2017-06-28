package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;
import com.hackathon.gridlock.liftthyneighbour.vos.TechPark;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class OfferRide extends Activity {

    ArrayList<TechPark> techParks;
    private final String TIME_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.OfferRide";
    private final String DATE_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.OfferRide_date";
    private RequestQueue requestQueue;

    private static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());

        populateTechParkSpinner();
        setDefaultSelectedRadioButton();
    }

    private void setDefaultSelectedRadioButton() {
        RadioButton rb = (RadioButton) findViewById(R.id.rbORTechPark);
        RadioGroup rg = (RadioGroup) findViewById(R.id.rgOR);
        rg.check(rb.getId());
    }




    private void populateTechParkSpinner() {
        final Toast errorToast = Toast.makeText(getApplicationContext(),"Failed to fetch Tech Park List. Check Internet Connectivity.",Toast.LENGTH_LONG);


        if (requestQueue != null) {
            String baseUrl = getString(R.string.BASE_URL);
            String targetUrl = baseUrl + "/api/getTechParkList";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.POST, targetUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null ) {
                                    Gson gson = new Gson();
                                    TechPark[] techParkList = gson.fromJson(response.getJSONArray("techParkList").toString(), TechPark[].class );
                                    ArrayList<TechPark> techParks = new ArrayList<TechPark>();
                                    int numTechParks = techParkList.length;
                                    for (int i = 0; i < numTechParks; i++ ) {
                                        techParks.add(techParkList[i]);
                                    }
                                    populateSpinner(techParks);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorToast.show();
                        }
                    }
            );

            jsObjRequest.setShouldCache(false);
            requestQueue.add(jsObjRequest);
        }
    }

    // Populates the tech park spinner with data
    private void populateSpinner(ArrayList<TechPark> tps) {
        Spinner techParkSpinner = (Spinner) findViewById(R.id.spinnerTechPark);
        this.techParks = tps;

        ArrayList<String> techParkNames = new ArrayList<String>();
        for (TechPark techPark: techParks ) {
            techParkNames.add(techPark.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,techParkNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        techParkSpinner.setAdapter(spinnerAdapter);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new OfferRideTimePickerFragment();
        newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new OfferRideDatePickerFragment();
        newFragment.show(getFragmentManager(), DATE_PICKER_FRAGMENT_TAG);
    }

    public void onOfferRideButtonClicked(View v) {
        int techParkId = getTechParkId();
        if (techParkId == -1  ) {
            Toast.makeText(getApplicationContext(), "Error choosing tech park. Try again.", Toast.LENGTH_LONG).show();
            redirectToUserHomeActivity();
            return;
        }

        String pickUpTimeInSecsUTC = getPickUpTime();
        int sourceType = getSourceType();
        int numSeats = getNumSeats();

        //TODO API Call to book ride and display toast
    }

    private int getNumSeats() {
        EditText etNumSeats = (EditText) findViewById(R.id.etORVacantSeats);
        return Integer.parseInt(etNumSeats.getText().toString());
    }

    private int getSourceType() {
        RadioGroup destinationTypeRg = (RadioGroup) findViewById(R.id.rgOR);
        String radioButtonText = ((RadioButton) findViewById(destinationTypeRg.getCheckedRadioButtonId())).getText().toString();
        int sourceType = 0;
        if (radioButtonText.equals("Tech Park")) {
            sourceType = 1;
        }
        else {
            sourceType = 0;
        }
        return sourceType;
    }

    private String getPickUpTime() {

        // Retrieve time info from edittext
        EditText etPickUpTime = (EditText) findViewById(R.id.etORPickUpTime);
        String rawTimeString = etPickUpTime.getText().toString();

        //sanity checks
        if (rawTimeString == null || rawTimeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Pick up time", Toast.LENGTH_LONG).show();
            return null;
        }


        String[] timeParts = rawTimeString.split(":");
        if (timeParts.length != 2) {
            Log.e("OfferRide ", "Error retrieving pickUpTimeDate");
            return null;
        }


        // Retrieve date from edittext
        EditText etPickUpDate = (EditText) findViewById(R.id.etORPickUpDate);
        String rawDateString = etPickUpDate.getText().toString();

        //sanity checks
        if (rawDateString == null || rawDateString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Pick up time", Toast.LENGTH_LONG).show();
            return null;
        }

        String[] dateParts = rawDateString.split("-");
        if (dateParts.length != 3) {
            Log.e("OfferRide ", "Error retrieving pickUpDate");
            return null;
        }

        if( Integer.parseInt(dateParts[0]) < 10 ) {
            dateParts[0] = "0" + dateParts[0];
        }
        if ( Integer.parseInt(dateParts[1]) < 10) {
            dateParts[1] = "0" + dateParts[1];
        }

        if (Integer.parseInt(timeParts[0]) < 10 ) {
            timeParts[0] = "0" + timeParts[0];
        }
        if (Integer.parseInt(timeParts[1]) < 10 ) {
            timeParts[1] = "0" + timeParts[1];
        }

        String dateTime = dateParts[2] + dateParts[1] + dateParts[0]
                + timeParts[0] + timeParts[1];

        DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        try {
            Date date = df.parse(dateTime);
            long secs = (date.getTime() / 1000 );
            return Long.toString(secs);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }



    private void redirectToUserHomeActivity() {
        Intent i = new Intent(this, UserHomeActivity.class);
        startActivity(i);
    }

    private int getTechParkId() {
        Spinner techParkSpinner = (Spinner) findViewById(R.id.spinnerTechPark);
        int position = techParkSpinner.getSelectedItemPosition();
        if (techParks == null) {
            return -1;
        }
        else {
            return techParks.get(position).getId();
        }
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
