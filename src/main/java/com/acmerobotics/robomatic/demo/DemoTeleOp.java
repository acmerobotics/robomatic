package com.acmerobotics.robomatic.demo;

import com.acmerobotics.robomatic.util.StickyGamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@TeleOp(name="DemoTeleOp")
public class DemoTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        DemoRobot robot = new DemoRobot(this);
        StickyGamepad stickyGamepad1 = new StickyGamepad(gamepad1);

        telemetry.addLine("init");
        telemetry.update();

        waitForStart();

        telemetry.clear();
        telemetry.addLine("start");
        telemetry.update();

        while (!isStopRequested()) {
            stickyGamepad1.update();

            robot.subsystem.setMotorVelocity(gamepad1.left_stick_y);
            if (stickyGamepad1.left_bumper) robot.subsystem.servoPositionOne();
            else if (stickyGamepad1.right_bumper) robot.subsystem.servoPositionTwo();

            robot.update();
        }

    }
}
