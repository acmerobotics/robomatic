package com.acmerobotics.robomatic.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.acmerobotics.dashboard.config.reflection.ClasspathScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigurationLoader {

    public static final String PREFS_NAME = "opmode";
    public static final String TAG = "ConfigurationLoader";

    private static final Set<String> IGNORED_PACKAGES = new HashSet<>(Arrays.asList(
            "java",
            "android",
            "com.sun",
            "com.vuforia",
            "com.google",
            "kotlin",
            "org.firstinspires",
            "com.acmerobotics.dashboard",
            "com.acmerobotics.roadrunner",
            "com.fasterxml",
            "fi",
            "org.apache",
            "org.intellij",
            "org.jetbrains",
            "org.opencv",
            "org.yaml",
            "jdk",
            "com.qualcomm"
    ));

    private SharedPreferences sharedPreferences;
    private Context context;
    private Class<?> configClass;

    private List<ConfigurationField> configurationFields;

    public ConfigurationLoader (Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        configurationFields = new ArrayList<>();

        ClasspathScanner scanner = new ClasspathScanner(new ClasspathScanner.Callback() {
            @Override
            public boolean shouldProcessClass(String className) {
                for (String packageName: IGNORED_PACKAGES)
                    if (className.startsWith(packageName))
                        return false;
                return true;
            }

            @Override
            public void processClass(Class klass) {
                Log.i(TAG, klass.getName());
                if (klass.isAnnotationPresent(OpmodeConfiguration.class)) {
                    processConfiguration(klass);
                }
            }
        });

        scanner.scanClasspath();
    }

    public List<View> getViews () {
        List<View> views = new ArrayList<>(configurationFields.size());
        for (ConfigurationField field: configurationFields) views.add(field.getView(context));
        return views;
    }

    public Object getConfig () {
        try {
            Constructor<?> constructor = configClass.getConstructor();
            Object object = constructor.newInstance();
            for (ConfigurationField field: configurationFields)
                field.apply(object);
            return object;
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    private void processConfiguration (Class klass) {
        configClass = klass;
        Field[] fields = klass.getFields();
        for (Field field: fields) {
            if (field.getType().isEnum()){
                EnumConfigurationField enumField = new EnumConfigurationField(field, sharedPreferences);
                configurationFields.add(enumField);
            }
            else if (field.getType() == Integer.TYPE && field.isAnnotationPresent(IntegerConfiguration.class)) {
                IntegerConfigurationField intField = new IntegerConfigurationField(field, sharedPreferences, field.getAnnotation(IntegerConfiguration.class).max());
                configurationFields.add(intField);
            }
            else if (field.getType() == Boolean.TYPE) {
                BooleanConfigurationField boolField = new BooleanConfigurationField(field, sharedPreferences);
                configurationFields.add(boolField);
            }
        }
    }

}
