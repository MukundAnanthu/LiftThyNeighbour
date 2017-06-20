package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hackathon.gridlock.liftthyneighbour.vos.Apartment;

import java.lang.reflect.Array;
import java.util.ArrayList;


/*
* Activity responsible for sign up actions
* */
public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //TODO Fill up Apartment Name spinner from API response
        ArrayList<Apartment> apartments = getApartmentList();
        populateApartmentSpinnerInUI(apartments);
    }

    private void populateApartmentSpinnerInUI(ArrayList<Apartment> apartments) {
        Spinner apartmentSpinner = (Spinner) findViewById(R.id.spinnerApartmentNames);
        ArrayList<String> apartmentNames = new ArrayList<String>();

        for (Apartment apartment: apartments ) {
            apartmentNames.add(apartment.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,apartmentNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apartmentSpinner.setAdapter(spinnerAdapter);
    }

    private ArrayList<Apartment> getApartmentList() {

        // Dummy data
        ArrayList<Apartment> apartments = new ArrayList<Apartment>();
        Apartment one = new Apartment(1,"A");
        Apartment two = new Apartment(2, "B");
        apartments.add(one);
        apartments.add(two);
        return apartments;

        //TODO Verify if API hit works
    }
}
