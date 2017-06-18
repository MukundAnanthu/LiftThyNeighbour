package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        //TODO API hit to authenticate admin and store admin token
        return true;
    }

    private boolean authenticateUser(String userName, String password) {
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
