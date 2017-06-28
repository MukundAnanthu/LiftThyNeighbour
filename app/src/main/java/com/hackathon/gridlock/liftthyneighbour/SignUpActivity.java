package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackathon.gridlock.liftthyneighbour.vos.Apartment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
        final Toast errorToast = Toast.makeText(getApplicationContext(),"Failed to fetch Apartment List. Check Internet Connectivity.",Toast.LENGTH_LONG);

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

        final Toast errorToast = Toast.makeText(getApplicationContext(), "Failed to Sign Up! Try again.", Toast.LENGTH_LONG);
        final Toast successToast = Toast.makeText(getApplicationContext(), "Sign up successful! Approval pending.", Toast.LENGTH_LONG);
        EditText etUserName = (EditText) findViewById(R.id.etSignUpUserName);
        String userName = etUserName.getText().toString();
        EditText etPassword = (EditText) findViewById(R.id.etSignUpPassword);
        String password = etPassword.getText().toString();
        Spinner spApartment = (Spinner) findViewById(R.id.spinnerApartmentNames);
        int apartmentId = apartmentList.get(spApartment.getSelectedItemPosition()).getId();
        EditText etContactNumber = (EditText) findViewById(R.id.etSignUpContactNumber);
        String contactNumber = etContactNumber.getText().toString();
        EditText etFlatNumber = (EditText) findViewById(R.id.etSignUpFlatNumber);
        String flatNumber = etFlatNumber.getText().toString();
        EditText etEmail = (EditText) findViewById(R.id.etSignUpEmail);
        String email = etEmail.getText().toString();
        EditText etVehicleNumber = (EditText) findViewById(R.id.etSignUpVehicleNumber);
        String vehicleNumber = etVehicleNumber.getText().toString();

        if (requestQueue != null ) {
            String baseUrl = getString(R.string.BASE_URL);
            String targetUrl = baseUrl + "/api/signup";

            HashMap<String, String> requestBody = new HashMap<String, String>();
            requestBody.put("userName",userName);
            requestBody.put("password",password);
            requestBody.put("apartmentId",Integer.toString(apartmentId));
            requestBody.put("contactNumber",contactNumber);
            requestBody.put("flatNumber",flatNumber);
            requestBody.put("email",email);
            requestBody.put("vehicleNumber",vehicleNumber);


            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(requestBody), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String authenticationStatus = response.getString("result");
                                if (authenticationStatus.equals("SUCCESS")) {
                                    successToast.show();
                                }
                                else {
                                    errorToast.show();
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
