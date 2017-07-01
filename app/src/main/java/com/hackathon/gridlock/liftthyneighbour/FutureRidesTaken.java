package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;
import com.hackathon.gridlock.liftthyneighbour.vos.Ride;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FutureRidesTaken extends Activity {

    private RequestQueue requestQueue;
    private ArrayList<Ride> rides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_rides_taken);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());

        populateFutureRidesList();
    }

    private void populateFutureRidesList() {
        String token = getUserToken();
        int userId = getUserId();

        final Toast errorToast = Toast.makeText(getApplicationContext(), "Couldn't fetch future rides list. Check Internet connectivity.", Toast.LENGTH_LONG);

        if (userId == -1 ) {
            Log.e("Future Rides Taken: ", "No user Id found in shared preferences file");
            return;
        }

        if (requestQueue != null) {
            String baseUrl = getResources().getString(R.string.BASE_URL);
            String targetUrl = baseUrl + getResources().getString(R.string.API_GET_FUTURE_RIDES_TAKEN);
            HashMap<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("userId",userId);
            requestBody.put("token",token);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(requestBody), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response != null ) {
                                    Gson gson = new Gson();
                                    Ride[] rideArray = gson.fromJson(response.getJSONArray("ridesTake").toString(), Ride[].class);
                                    ArrayList<Ride> rides = new ArrayList<Ride>();
                                    int numRides = rideArray.length;
                                    for (int i = 0; i < numRides; i++ ) {
                                        rides.add(rideArray[i]);
                                    }
                                    populateList(rides);
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

    private void populateList(ArrayList<Ride> rides) {
        ListView ridesList = (ListView) findViewById(R.id.lvFutureRidesTaken);
        ArrayList<String> rideListItem = new ArrayList<String>();

        this.rides = rides;

        for (Ride ride : rides) {
            String toDisplay = ride.getDriverName() + "\n" + getFormattedDateAndTime(ride.getTimestamp());
            rideListItem.add(toDisplay);
        }

        // Populate the listView
        ArrayAdapter<String> rideListAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                rideListItem
        );

        ridesList.setAdapter(rideListAdapter);


        //set listener
        ridesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRideDetails(position);
            }
        });
    }

    private String getFormattedDateAndTime(String ts) {
        Long tsL = Long.parseLong(ts+"000");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date(tsL));
    }


    private void showRideDetails(int position) {
        Ride ride = this.rides.get(position);
        Intent i = new Intent(this, FutureRideTakenDetails.class);
        i.putExtra(FutureRideTakenDetails.EXTRA_DRIVER_NAME, ride.getDriverName());
        i.putExtra(FutureRideTakenDetails.EXTRA_CONTACT_NUM, ride.getContactNumber());
        i.putExtra(FutureRideTakenDetails.EXTRA_VEHICLE_NUM, ride.getVehicleNumber());
        i.putExtra(FutureRideTakenDetails.EXTRA_SRC_NAME, ride.getSrcName());
        i.putExtra(FutureRideTakenDetails.EXTRA_DEST_NAME, ride.getDestinationName());
        i.putExtra(FutureRideTakenDetails.EXTRA_TIMESTAMP, ride.getTimestamp());
        startActivity(i);
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
