package com.acmerobotics.robomatic.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.acmerobotics.robomatic.R;

public class OpModeConfigurationActivity extends Activity {

    private LinearLayout layout;

    public static void  populateMenu(Menu menu, Activity activity) {
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, 700, "Configure OpMode");
        item.setVisible(true);
        item.setOnMenuItemClickListener((menuItem) -> {
            Intent intent = new Intent(activity, OpModeConfigurationActivity.class);
            activity.startActivity(intent);
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

        setContentView(R.layout.activity_opmode_configuration);
        layout = findViewById(R.id.opmode_config);
        layout.setPadding(25, 25, 25, 25);

        ConfigurationLoader loader = new ConfigurationLoader(this);
        for (View view: loader.getViews()) addView(view);
    }

    private void addView (View view) {
        layout.addView(view);
        layout.invalidate();
    }

}
