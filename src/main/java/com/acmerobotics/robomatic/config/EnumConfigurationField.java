package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumConfigurationField extends ConfigurationField{

    public EnumConfigurationField (Field field, SharedPreferences preferences) {
        super(field, preferences);
    }

    public List<String> getConstants () {
        List<Object> constants = Arrays.asList(field.getType().getEnumConstants());
        List<String> strings = new ArrayList<>(constants.size());
        for (Object constant: constants) {
            strings.add(constant.toString());
        }
        return strings;
    }

    @Override
    public View getView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView text = new TextView(context);
        text.setTextSize(20);
        text.setText(getName() + ": ");

        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, getConstants());
        spinner.setAdapter(adapter);
        spinner.setSelection(getConstants().indexOf(getSavedPreference()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                putString(getName(), getConstants().get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        layout.addView(text);
        layout.addView(spinner);
        layout.invalidate();
        return layout;
    }

    @Override
    public void apply(Object object) {
        try {
            field.set(object, Enum.valueOf((Class<Enum>) field.getType(), getSavedPreference()));
        } catch (Exception e) {
            Log.e(ConfigurationLoader.TAG, e.getLocalizedMessage());
        }
    }

    public String getSavedPreference () {
        return sharedPreferences.getString(getName(), getConstants().get(0));
    }
}
