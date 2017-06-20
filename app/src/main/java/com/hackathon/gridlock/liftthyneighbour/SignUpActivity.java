package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;
import com.hackathon.gridlock.liftthyneighbour.vos.Apartment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/*
* Activity responsible for sign up actions
* */
public class SignUpActivity extends Activity {

    private RequestQueue requestQueue;
    private ArrayList<Apartment> apartmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());

        populateApartmentSpinner();

        // Sign Up Button Click Listener
        Button signUpButton = (Button) findViewById(R.id.bSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSignUpAPICall();
            }
        });
    }

    private void populateApartmentSpinner() {
        final Toast errorToast = Toast.makeText(getApplicationContext(),"Failed to fetch Apartment List",Toast.LENGTH_LONG);

        if (requestQueue != null) {
            String baseUrl = getString(R.string.BASE_URL);
            String targetUrl = baseUrl + "/api/getApartmentList";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.POST, targetUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null ) {
                                    Gson gson = new Gson();
                                    Apartment[] apartmentList = gson.fromJson(response.getJSONArray("apartmentList").toString(), Apartment[].class );
                                    ArrayList<Apartment> apartments = new ArrayList<Apartment>();
                                    int numApartments = apartmentList.length;
                                    for (int i = 0; i < numApartments; i++ ) {
                                        apartments.add(apartmentList[i]);
                                    }
                                    populateApartmentSpinnerInUI(apartments);
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


    private void makeSignUpAPICall() {
        //TODO Make Sign Up API call
        // if call fails, toast error, else toast success
    }

    private void populateApartmentSpinnerInUI(ArrayList<Apartment> apartments) {
        Spinner apartmentSpinner = (Spinner) findViewById(R.id.spinnerApartmentNames);
        ArrayList<String> apartmentNames = new ArrayList<String>();

        // for fetching apartment id when sending API hit for Form Sign Up
        this.apartmentList = apartments;

        for (Apartment apartment: apartments ) {
            apartmentNames.add(apartment.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,apartmentNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apartmentSpinner.setAdapter(spinnerAdapter);
    }
}
