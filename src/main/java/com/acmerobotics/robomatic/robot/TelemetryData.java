package com.acmerobotics.robomatic.robot;

import java.util.HashMap;
import java.util.Map;

public class TelemetryData {

    private String prefix;
    private Map<String, Object> data;

    public TelemetryData (String prefix) {
        this.prefix = prefix;
        data = new HashMap<>();
    }

    public void addData (String label, Object value) {
        data.put(prefix + '_' + label, value);
    }

    public Map<String, Object> getData () {
        return data;
    }
}
