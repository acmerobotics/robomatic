package com.acmerobotics.robomatic.config;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.qualcomm.ftcrobotcontroller.R;

public class OpModeConfigurationActivity extends Activity {

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

        setContentView(R.layout.activity_opmode_configuration);
        layout = (LinearLayout) findViewById(R.id.opmode_config);
        layout.setPadding(25, 25, 25, 25);

        ConfigurationLoader loader = new ConfigurationLoader(this);
        for (View view: loader.getViews()) addView(view);
    }

    private void addView (View view) {
        layout.addView(view);
        layout.invalidate();
    }

}
