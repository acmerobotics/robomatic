package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;

public class BooleanConfigurationField extends ConfigurationField{

    public BooleanConfigurationField (Field field, SharedPreferences preferences) {
        super(field, preferences);
    }

    @Override
    public View getView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView text = new TextView(context);
        text.setTextSize(20);
        text.setText(getName() + ": ");
        layout.addView(text);

        Switch toggle = new Switch(context);
        toggle.setTextOn("true");
        toggle.setTextOff("false");
        toggle.setChecked(getSavedPreference());
        toggle.setOnCheckedChangeListener((a, b) -> putBoolean(getName(), b));
        layout.addView(toggle);
        return layout;
    }

    @Override
    public void apply(Object object) {
        try {
            field.setBoolean(object, getSavedPreference());
        } catch (Exception e) {
            Log.e(ConfigurationLoader.TAG, e.getLocalizedMessage());
        }
    }

    private boolean getSavedPreference () {
        return sharedPreferences.getBoolean(getName(), false);
    }
}
