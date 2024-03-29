package com.hackathon.gridlock.liftthyneighbour;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferRideTimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public OfferRideTimePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        EditText pickUpTimeEt = (EditText) getActivity().findViewById(R.id.etORPickUpTime);
        String timeChosen = "";

        if (minute < 10) {
            timeChosen = Integer.toString(hourOfDay) + ":" + "0"+ Integer.toString(minute);
        }
        else {
            timeChosen = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
        }
        pickUpTimeEt.setText(timeChosen);
    }


}