package com.hackathon.gridlock.liftthyneighbour;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */

public class TakeRideDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public TakeRideDatePickerFragment() {
        // Required empty public constructor
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        EditText pickUpDateEditText = (EditText) getActivity().findViewById(R.id.etTRPickUpDate);
        String dateChosen = Integer.toString(dayOfMonth) + "-" + Integer.toString(month+1) + "-" +
                Integer.toString(year);
        pickUpDateEditText.setText(dateChosen);
    }
}