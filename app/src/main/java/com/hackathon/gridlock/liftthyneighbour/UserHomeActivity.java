package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class UserHomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        ListView userOpsListView = (ListView) findViewById(R.id.lvUserOps);
        userOpsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchToOp(position);
            }
        });
    }

    private void switchToOp(int position) {
        switch (position) {
            case 0:
                Intent takeRideIntent = new Intent(this, TakeRide.class);
                startActivity(takeRideIntent);
                break;
            case 1:
                Intent offerRideIntent = new Intent(this, OfferRide.class);
                startActivity(offerRideIntent);
                break;
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
