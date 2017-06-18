package com.hackathon.gridlock.liftthyneighbour;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.logging.Logger;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        TextView tvNewUser = (TextView) findViewById(R.id.tvNewUser);
        tvNewUser.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

    }
}
