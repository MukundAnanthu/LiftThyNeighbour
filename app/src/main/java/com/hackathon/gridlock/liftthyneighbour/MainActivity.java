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
import com.android.volley.toolbox.Volley;
import com.hackathon.gridlock.liftthyneighbour.util.RequestQueueProviderSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private RequestQueue requestQueue;


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

            final Intent redirectToAdminHomeIntent = new Intent(this, AdminHomeActivity.class);
            final Intent redirectToUserHomeIntent = new Intent(this, UserHomeActivity.class);

            buttonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAdmin = checkBoxLogInAsAdmin.isChecked();
                    String userName = editTextUserName.getText().toString();
                    String password = editTextPassword.getText().toString();
                    if(isAdmin) {
                        boolean isAdminAuthenticated = authenticateAdmin(userName,password);
                        if (isAdminAuthenticated) {
                            startActivity(redirectToAdminHomeIntent);
                        } else {
                            CharSequence toastMessage = (CharSequence) getResources().getString(R.string.AUTHENTICATION_FAILED);
                            Toast authenticationFailedToast = Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG);
                            authenticationFailedToast.show();

                        }
                    }
                    else {
                        boolean isUserAuthenticated = authenticateUser(userName,password);
                        if(isUserAuthenticated) {
                            startActivity(redirectToUserHomeIntent);
                        }
                        else {
                            CharSequence toastMessage = (CharSequence) getResources().getString(R.string.AUTHENTICATION_FAILED);
                            Toast authenticationFailedToast = Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG);
                            authenticationFailedToast.show();
                        }
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

    private boolean authenticateAdmin(String userName, String password) {
        String userType = getResources().getString(R.string.USER_TYPE_ADMIN);
        //TODO API hit to authenticate admin and store admin token

        if (requestQueue!=null) {
            String baseUrl = getResources().getString(R.string.BASE_URL);
            String targetUrl = baseUrl + getResources().getString(R.string.API_LOGIN);

            Log.i("TargetURL: ",targetUrl);

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("userId",userName);
            hm.put("password",password);
            hm.put("userType",userType);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, targetUrl, new JSONObject(hm), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response: ",response.toString());
                            try {
                                Log.i("Response:state: ", response.getString("result"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("ResponseERrrrroor:",error.toString());

                        }
                    }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };

            requestQueue.add(jsObjRequest);
        }

        return true;
    }

    private boolean authenticateUser(String userName, String password) {
        String userType = getResources().getString(R.string.USER_TYPE_NORMAL);
        //TODO API hit to authenticate user and store user token



        return true;
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


}
