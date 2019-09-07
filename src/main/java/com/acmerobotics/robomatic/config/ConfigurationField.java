package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.lang.reflect.Field;

public abstract class ConfigurationField {
    protected Field field;
    SharedPreferences sharedPreferences;

    protected ConfigurationField () {}

    ConfigurationField(Field field, SharedPreferences preferences) {
        this.field = field;
        this.sharedPreferences = preferences;
    }

    public abstract View getView (Context context);

    public abstract void apply (Object object);

    void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getName () {
        return field.getName();
    }
}
