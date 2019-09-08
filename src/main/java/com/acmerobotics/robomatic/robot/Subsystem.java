package com.acmerobotics.robomatic.robot;

import com.acmerobotics.dashboard.canvas.Canvas;

public abstract class Subsystem {

    protected TelemetryData telemetryData;

    protected Subsystem(String telemetryPrefix) {
        telemetryData = new TelemetryData(telemetryPrefix);
    }

    public abstract void update(Canvas fieldOverlay);

}
