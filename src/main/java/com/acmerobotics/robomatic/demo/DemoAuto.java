package com.acmerobotics.robomatic.demo;

import com.acmerobotics.robomatic.config.ConfigurationLoader;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//@Autonomous(name="demoAuto")
public class DemoAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        DemoRobot robot = new DemoRobot(this, false);

        DemoConfig config = (DemoConfig) new ConfigurationLoader(hardwareMap.appContext).getConfig();

        robot.addTelemetry("color", config.color);
        robot.addTelemetry("delay", config.delay);
        robot.addTelemetry("latched", config.latched);

        robot.runUntil(this::opModeIsActive);

        robot.subsystem.servoPositionOne();
        robot.subsystem.setMotorVelocity(1);

        robot.runUntil(robot.subsystem::digitalSensorTriggered);

        robot.subsystem.setMotorVelocity(-1);

        robot.runForTime(5000);

        robot.subsystem.servoPositionTwo();

        robot.runUntilStop();

    }
}
