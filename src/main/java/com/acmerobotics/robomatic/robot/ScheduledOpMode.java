package com.acmerobotics.robomatic.robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class ScheduledOpMode extends LinearOpMode {

    public boolean stopRequested () {
        return isStopRequested();
    }

}
