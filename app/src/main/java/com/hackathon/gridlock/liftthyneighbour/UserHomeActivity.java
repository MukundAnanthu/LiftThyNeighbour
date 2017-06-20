package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
}
