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

import com.android.volley.AuthFailureError;
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


import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TakeRide extends Activity {

    private static final String RIDE_TYPE_TAKE = "TAKE";
    ArrayList<TechPark> techParks;
    private final String TIME_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.TakeRide";
    private final String DATE_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.TakeRide_date";
    private RequestQueue requestQueue;

    private static final int SENTINEL_NUM_SEATS = -1;
    private static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_ride);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());


        populateTechParkSpinner();
        setDefaultSelectedRadioButton();
    }

    public void setDefaultSelectedRadioButton() {
        RadioButton rb = (RadioButton) findViewById(R.id.rbTRTechPark);
        RadioGroup rg = (RadioGroup) findViewById(R.id.rgTR);
        rg.check(rb.getId());
    }

    private void populateTechParkSpinner() {
        final Toast errorToast = Toast.makeText(getApplicationContext(),"Couldn't fetch Tech Park List. Check Internet connectivity.",Toast.LENGTH_LONG);

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
        Spinner techParkSpinner = (Spinner) findViewById(R.id.spinnerTRTechPark);
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
        DialogFragment newFragment = new TakeRideTimePickerFragment();
        newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TakeRideDatePickerFragment();
        newFragment.show(getFragmentManager(), DATE_PICKER_FRAGMENT_TAG);
    }

    public void onTakeRideButtonClicked(View v) {
        int userId = getUserId();
        if (userId == -1 ) {
            Log.e("OfferRide: ", "No user Id found in shared preferences file");
            return;
        }

        String token = getUserToken();
        int techParkId = getTechParkId();
        if (techParkId == -1  ) {
            Toast.makeText(getApplicationContext(), "Error choosing tech park. Try again.", Toast.LENGTH_LONG).show();
            redirectToUserHomeActivity();
            return;
        }

        //pickuptime includes both time and date of pick up
        String pickUpTimeInSecsUTC = getPickUpTime();
        int sourceType = getSourceType();
        int numSeats = SENTINEL_NUM_SEATS;

        //Make API call
        final Toast errorToast = Toast.makeText(getApplicationContext(), "Couldn't Book Ride. Try again. Check Internet connectivity.", Toast.LENGTH_LONG);
        final Toast noMatchToast = Toast.makeText(getApplicationContext(), "Couldn't find a ride. Try again later.", Toast.LENGTH_LONG);
        final Toast tooLateToast = Toast.makeText(getApplicationContext(), "Must book ride atleast one hour before!", Toast.LENGTH_LONG);
        if (requestQueue != null) {
            String baseUrl = getResources().getString(R.string.BASE_URL);
            String targetUrl = baseUrl + getResources().getString(R.string.API_RIDE);
            HashMap<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("userId",userId);
            requestBody.put("token",token);
            requestBody.put("sourceType", sourceType);
            requestBody.put("techParkId", techParkId);
            requestBody.put("timestamp",pickUpTimeInSecsUTC);
            requestBody.put("type", RIDE_TYPE_TAKE);
            requestBody.put("numberOfSeats", numSeats);


            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(requestBody), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response != null) {
                                    String status = response.getString("result");
                                    if (status.equals("SUCCESS")) {
                                        String driverName = response.getString("driverName");
                                        String contactNumber = response.getString("contactNumber");
                                        String vehicleNumber = response.getString("vehicleNumber");
                                        String departureTime = response.getString("departureTime");
                                        redirectTobookedRideDetailsPage(driverName, contactNumber, vehicleNumber, departureTime);
                                    } else {
                                        String tooLate = "Ride should be offered or taken atleast one hour before";
                                        if (response.getString("message").equals(tooLate)) {
                                            tooLateToast.show();
                                        } else {
                                            noMatchToast.show();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                errorToast.show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorToast.show();
                        }
                    }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };
            jsObjRequest.setShouldCache(false);
            requestQueue.add(jsObjRequest);
        }
        //TODO API request
    }

    private void redirectTobookedRideDetailsPage(String driverName, String contactNumber,String vehicleNumber,String departureTime) {
        Intent bookedRideDetails = new Intent(this, BookedRideDetails.class);
        bookedRideDetails.putExtra(BookedRideDetails.DRIVER_NAME, driverName);
        bookedRideDetails.putExtra(BookedRideDetails.CONTACT_NUMBER, contactNumber);
        bookedRideDetails.putExtra(BookedRideDetails.VEHICLE_NUMBER, vehicleNumber);
        bookedRideDetails.putExtra(BookedRideDetails.DEPARTURE_TIME, departureTime);
        startActivity(bookedRideDetails);
    }

    private String getUserToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String tokenKey = getResources().getString(R.string.KEY_TOKEN);
        return sharedPreferences.getString(tokenKey, "");
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String userIdKey = getResources().getString(R.string.KEY_USER_ID);
        return sharedPreferences.getInt(userIdKey,-1);
    }

    private int getSourceType() {
        RadioGroup destinationTypeRg = (RadioGroup) findViewById(R.id.rgTR);
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


    private void redirectToUserHomeActivity() {
        Intent i = new Intent(this, UserHomeActivity.class);
        startActivity(i);
    }

    private String getPickUpTime() {

        // Retrieve time info from edittext
        EditText etPickUpTime = (EditText) findViewById(R.id.etTRPickUpTime);
        String rawTimeString = etPickUpTime.getText().toString();

        //sanity checks
        if (rawTimeString == null || rawTimeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Pick up time", Toast.LENGTH_LONG).show();
            return null;
        }


        String[] timeParts = rawTimeString.split(":");
        if (timeParts.length != 2) {
            Log.e("TakeRide ", "Error retrieving pickUpTimeDate");
            return null;
        }


        // Retrieve date from edittext
        EditText etPickUpDate = (EditText) findViewById(R.id.etTRPickUpDate);
        String rawDateString = etPickUpDate.getText().toString();

        //sanity checks
        if (rawDateString == null || rawDateString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Pick up time", Toast.LENGTH_LONG).show();
            return null;
        }

        String[] dateParts = rawDateString.split("-");
        if (dateParts.length != 3) {
            Log.e("TakeRide ", "Error retrieving pickUpDate");
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

    private int getTechParkId() {
        Spinner techParkSpinner = (Spinner) findViewById(R.id.spinnerTRTechPark);
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
