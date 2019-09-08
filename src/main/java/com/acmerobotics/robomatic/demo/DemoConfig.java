package com.acmerobotics.robomatic.demo;

import com.acmerobotics.robomatic.config.IntegerConfiguration;
import com.acmerobotics.robomatic.config.OpmodeConfiguration;

@OpmodeConfiguration
public class DemoConfig {
    public enum AllianceColor {
        RED,
        BLUE,
    }

    public AllianceColor color;

    public enum StartLocation {
        CRATER,
        DEPOT
    }

    public StartLocation startLocation;

    public boolean latched;

    public boolean sampleBoth;

    public boolean playMusic;

    @IntegerConfiguration(max=10)
    public int delay;
}
