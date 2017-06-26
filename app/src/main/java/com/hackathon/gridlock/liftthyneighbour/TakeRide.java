package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;

public class TakeRide extends Activity {

    ArrayList<TechPark> techParks;
    private final String TIME_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.TakeRide";
    private final String DATE_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.TakeRide_date";
    private RequestQueue requestQueue;


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
        final Toast errorToast = Toast.makeText(getApplicationContext(),"Failed to fetch Tech Park List",Toast.LENGTH_LONG);

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
        //TODO API request
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
