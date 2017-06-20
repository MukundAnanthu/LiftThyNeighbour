package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private RequestQueue requestQueue;
    private String adminAuthenticationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup request queue for API hits
        requestQueue = RequestQueueProviderSingleton.getRequestQueue(getApplicationContext());


        //display login screen if user not logged in
        if (!isUserSignedIn()) {

            //set up login screen
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            TextView tvNewUser = (TextView) findViewById(R.id.tvNewUser);
            tvNewUser.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            // Handle new user
            final Intent signUpIntent = new Intent(this, SignUpActivity.class);
            tvNewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(signUpIntent);
                }
            });

            // Handle sign in
            final CheckBox checkBoxLogInAsAdmin = (CheckBox) findViewById(R.id.checkBoxAdmin);
            final EditText editTextUserName = (EditText) findViewById(R.id.etUserName);
            final EditText editTextPassword = (EditText) findViewById(R.id.etPassword);
            Button buttonSignIn = (Button) findViewById(R.id.btSignIn);

            buttonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAdmin = checkBoxLogInAsAdmin.isChecked();
                    String userName = editTextUserName.getText().toString();
                    String password = editTextPassword.getText().toString();
                    if(isAdmin) {
                        authenticateAdmin(userName,password);
                    }
                    else {
                        authenticateUser(userName,password);
                    }
                }
            });


        } else {
            if (isUserAdmin()) {
                //redirect to admin home activity
                Intent intent = new Intent(this, AdminHomeActivity.class);
                startActivity(intent);
            } else {
                //redirect to user home activity
                Intent intent = new Intent(this, UserHomeActivity.class);
                startActivity(intent);
            }
        }
    }

    private void authenticateAdmin(String userName, String password) {
        final String userType = getResources().getString(R.string.USER_TYPE_ADMIN);
        final Toast loginErrorToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.AUTHENTICATION_FAILED), Toast.LENGTH_LONG);
        
        if (requestQueue != null) {
            String baseUrl = getResources().getString(R.string.BASE_URL);
            String targetUrl = baseUrl + getResources().getString(R.string.API_LOGIN);
            HashMap<String, String> requestBody = new HashMap<String, String>();
            requestBody.put("userId",userName);
            requestBody.put("password",password);
            requestBody.put("userType",userType);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(requestBody), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String authenticationStatus = response.getString("result");
                                if (authenticationStatus.equals("SUCCESS")) {
                                    setTokenAndUserType(response.getString("token"), userType);
                                    redirectToAdminHomePage();
                                }
                                else {
                                    loginErrorToast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loginErrorToast.show();
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

    private void authenticateUser(String userName, String password) {
        String userType = getResources().getString(R.string.USER_TYPE_NORMAL);
        //TODO API hit to authenticate user and store user token
    }

    private boolean isUserAdmin() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String isUserAdminKey = getResources().getString(R.string.KEY_IS_USER_ADMIN);
        return sharedPreferences.getBoolean(isUserAdminKey, false);
    }

    private boolean isUserSignedIn() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String isUserSignedKey = getResources().getString(R.string.KEY_IS_USER_SIGNED);
        return sharedPreferences.getBoolean(isUserSignedKey, false);
    }

    // switches from sign in activity to admin home screen
    private void redirectToAdminHomePage() {
        Intent i = new Intent(this, AdminHomeActivity.class);
        startActivity(i);
    }

    private void setTokenAndUserType(String token, String userType) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.KEY_TOKEN), token);
        editor.putString(getString(R.string.KEY_USER_TYPE),userType);
        editor.commit();

    }

}
