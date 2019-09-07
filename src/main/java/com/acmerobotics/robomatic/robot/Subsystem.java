package com.acmerobotics.robomatic.robot;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

public interface Subsystem {

    void update(TelemetryPacket packet);

}
