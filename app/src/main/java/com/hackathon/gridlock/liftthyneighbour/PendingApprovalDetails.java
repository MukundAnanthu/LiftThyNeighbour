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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;
import com.hackathon.gridlock.liftthyneighbour.vos.Tenant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PendingApprovalDetails extends Activity {

    public static final String KEY_USER_ID = "PendingApprovalDetails.USER_ID";
    public static final String KEY_USER_NAME = "PendingApprovalDetails.USER_NAME";
    public static final String KEY_USER_FLAT_NUMBER = "PendingApprovalDetails.FLAT_NUMBER";
    public static final String KEY_VEHICLE_NUMBER = "PendingApprovalDetails.VEHICLE_NUMBER";
    public static final String KEY_CONTACT_NUMBER = "PendingApprovalDetails.CONTACT_NUMBER";
    public static final String KEY_EMAIL = "PendingApprovalDetails.EMAIL";


    private int userId;
    private String userName;
    private String flatNumber;
    private String vehicleNumber;
    private long contactNumber;
    private String email;

    private RequestQueue requestQueue;


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
        setContentView(R.layout.activity_pending_approval_details);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());

        displayPendingApprovalDetails();
        setUpApproveButtonClickListener();
        setUpRejectButtonClickListener();
    }

    private void setUpApproveButtonClickListener() {
        Button approveButton = (Button) findViewById(R.id.bApprove);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendApprovalAPIrequest();
            }
        });
    }

    private void sendApprovalAPIrequest() {
        int userIdToApprove = getUserId();
        int adminUserId = getAdminUserId();
        String token = getAdminToken();

        if (adminUserId == -1 ) {
            Log.e("Approval", "No admin user Id found in shared preferences file");
            return;
        }


        final Toast errorToast = Toast.makeText(getApplicationContext(), "Failed to approve", Toast.LENGTH_LONG);
        final Toast successToast = Toast.makeText(getApplicationContext(), "Operation Successful", Toast.LENGTH_LONG);

        if (requestQueue != null ) {
            String baseUrl = getResources().getString(R.string.BASE_URL);
            String targetUrl = baseUrl + getResources().getString(R.string.API_APPROVAL);
            HashMap<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("userId",adminUserId);
            requestBody.put("token", token);
            requestBody.put("userIdToApprove",userIdToApprove);
            requestBody.put("approved","yes");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(requestBody), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response != null ) {
                                    String responseMessage = response.getString("message");
                                    Log.i("ResponseMessage: ", responseMessage);
                                    if (responseMessage.equals("Operation completed successfully")) {
                                        successToast.show();
                                        switchToPendingApprovalsActivity();
                                    }
                                    else {
                                        errorToast.show();
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
        //TODO send approval API request. Toast result
    }

    private int getAdminUserId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String userIdKey = getResources().getString(R.string.KEY_USER_ID);
        return sharedPreferences.getInt(userIdKey,-1);
    }

    private String getAdminToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String tokenKey = getResources().getString(R.string.KEY_TOKEN);
        return sharedPreferences.getString(tokenKey, "");
    }

    private void setUpRejectButtonClickListener() {
        Button rejectButton = (Button) findViewById(R.id.bReject);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRejectionAPIrequest();
            }
        });
    }


    private void sendRejectionAPIrequest() {
        //TODO send rejection API request. Toast result
    }


    private void displayPendingApprovalDetails() {
        Intent i = getIntent();
        setUserId(i.getIntExtra(PendingApprovalDetails.KEY_USER_ID, -1));
        setUserName(i.getStringExtra(PendingApprovalDetails.KEY_USER_NAME));
        setFlatNumber(i.getStringExtra(PendingApprovalDetails.KEY_USER_FLAT_NUMBER));
        setVehicleNumber(i.getStringExtra(PendingApprovalDetails.KEY_VEHICLE_NUMBER));
        setContactNumber(i.getIntExtra(PendingApprovalDetails.KEY_CONTACT_NUMBER,-1));
        setEmail(i.getStringExtra(PendingApprovalDetails.KEY_EMAIL));



        TextView tvName = (TextView) findViewById(R.id.tvPAName);
        tvName.setText(getUserName());
        TextView tvFlatNumber = (TextView) findViewById(R.id.tvPAFlatNumber);
        tvFlatNumber.setText(getFlatNumber());
        TextView tvVehicleNumber = (TextView) findViewById(R.id.tvPAVehicleNumber);
        tvVehicleNumber.setText(getVehicleNumber());
        TextView tvContactNumber = (TextView) findViewById(R.id.tvPAContactNumber);
        tvContactNumber.setText(Long.toString(getContactNumber()));
        TextView tvEmail = (TextView) findViewById(R.id.tvPAEmail);
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

    private void switchToPendingApprovalsActivity() {
        Intent i = new Intent(this, PendingApprovals.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}
