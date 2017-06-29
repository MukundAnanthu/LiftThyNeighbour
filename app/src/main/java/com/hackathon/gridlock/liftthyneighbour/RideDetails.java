package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.os.Bundle;


/*
*  Can be invoked by FutureRidesOffered and FutureRidesTaken activities. Has 2 different layouts. Layout decided
*  by the activity which sent Intent to RideDetails
* */
public class RideDetails extends Activity {

    public static final String EXTRA_VIEWER ="RideDetails.EXTRA_VIEWER";
    public static final String EXTRA_VIWER_DRIVER = "RideDetails.EXTRA_VIEWER_DRIVER";
    public static final String EXTRA_VIWER_PASSENGER = "RideDetails.EXTRA_VIEWER_PASSENGER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getStringExtra(EXTRA_VIEWER).equals(EXTRA_VIWER_DRIVER)) {
            setContentView(R.layout.activity_ride_details_for_driver);
            //TODO populate the view elements from Intent Extras
        }
        else {
            setContentView(R.layout.activity_ride_details_for_passenger);
            //TODO populate the view elements from Intent Extras
        }
    }
}
