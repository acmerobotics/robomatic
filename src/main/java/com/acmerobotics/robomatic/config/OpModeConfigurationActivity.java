package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.firstinspires.ftc.ftccommon.external.OnCreateMenu;

import com.acmerobotics.robomatic.R;

public class OpModeConfigurationActivity extends Activity {

    private LinearLayout layout;

    @OnCreateMenu
    public static void  populateMenu(Context context, Menu menu) {
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, 700, "Configure OpMode");
        item.setVisible(true);
        item.setOnMenuItemClickListener((menuItem) -> {
            Intent intent = new Intent(context, OpModeConfigurationActivity.class);
            context.startActivity(intent);
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
