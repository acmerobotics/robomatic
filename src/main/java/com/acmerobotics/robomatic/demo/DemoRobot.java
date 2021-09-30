package com.acmerobotics.robomatic.demo;

import com.acmerobotics.robomatic.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class DemoRobot extends Robot {

    public final DemoSubsystem subsystem;

    public DemoRobot(LinearOpMode opmode, boolean inTeleOp) {
        super(opmode, inTeleOp);

        registerHub("hub0");

        subsystem = new DemoSubsystem(this);
        registerSubsytem(subsystem);
    }

}
