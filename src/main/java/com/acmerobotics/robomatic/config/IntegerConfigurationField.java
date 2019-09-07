package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;

public class IntegerConfigurationField extends ConfigurationField{

    private int max;

    public IntegerConfigurationField(Field field, SharedPreferences sharedPreferences, int max) {
        super (field, sharedPreferences);
        this.max = max;
    }

    @Override
    public View getView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView text = new TextView(context);
        text.setText(getName() + ": " + getSavedPreference());
        text.setTextSize(20);
        layout.addView(text);

        SeekBar bar = new SeekBar(context);
        bar.setMax(max);
        bar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bar.setProgress(getSavedPreference());
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                putInt(getName(), i);
                text.setText(getName() + ": " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        layout.addView(bar);
        layout.invalidate();
        return layout;
    }

    @Override
    public void apply(Object object) {
        try {
            field.setInt(object, getSavedPreference());
        } catch (Exception e) {
            Log.e(ConfigurationLoader.TAG, e.getLocalizedMessage());
        }
    }

    private int getSavedPreference () {
        return sharedPreferences.getInt(getName(), 0);
    }

}
