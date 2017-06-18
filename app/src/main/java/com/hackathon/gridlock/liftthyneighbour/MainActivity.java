package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


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


    private boolean isUserAdmin() {
        return true;
    }

    private boolean isUserSignedIn() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_NAME), Context.MODE_PRIVATE);
        String isUserSignedKey = getResources().getString(R.string.KEY_IS_USER_SIGNED);
        return sharedPreferences.getBoolean(isUserSignedKey, false);
    }
}
