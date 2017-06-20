package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hackathon.gridlock.liftthyneighbour.vos.TechPark;

import java.util.ArrayList;

public class OfferRide extends Activity {

    ArrayList<TechPark> techParks;
    private final String TIME_PICKER_FRAGMENT_TAG = "com.hackathon.gridlock.liftthyneighbour.OfferRide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);


        populateTechParkSpinner();
    }

    private void populateTechParkSpinner() {
        //TODO API call to get list of Tech Parks

        //dummy data
        TechPark one = new TechPark(1, "Bagmane");
        TechPark two = new TechPark(2, "RMZ");

        ArrayList<TechPark> techParks = new ArrayList<TechPark>();
        techParks.add(one);
        techParks.add(two);

        //to be called from within onResponse
        populateSpinner(techParks);
    }

    // Populates the tech park spinner with data
    private void populateSpinner(ArrayList<TechPark> tps) {
        Spinner techParkSpinner = (Spinner) findViewById(R.id.spinnerTechPark);
        this.techParks = tps;

        ArrayList<String> techParkNames = new ArrayList<String>();
        for (TechPark techPark: techParks ) {
            techParkNames.add(techPark.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,techParkNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        techParkSpinner.setAdapter(spinnerAdapter);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new OfferRideTimePickerFragment();
        newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
    }

}
