package com.acmerobotics.robomatic.demo;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.robomatic.hardware.CachingSensor;
import com.acmerobotics.robomatic.robot.Robot;
import com.acmerobotics.robomatic.robot.Subsystem;
import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

public class DemoSubsystem extends Subsystem {

    private DcMotorEx motor;
    private Servo servo;
    private DigitalChannel digitalChannel;
    private AnalogSensor analogSensor;
    private CachingSensor imuSensor;

    public DemoSubsystem(Robot robot) {
        super("demo");

        motor = robot.getMotor("motor");
        servo = robot.getServo("servo");
        digitalChannel = robot.getDigitalChannel("digital");
        analogSensor = robot.getAnalogSensor("analog");
        BNO055IMUImpl imu = robot.getRevHubImu(0, new Robot.Orientation(Robot.Axis.POSITIVE_X, Robot.Axis.POSITIVE_Y, Robot.Axis.POSITIVE_Z));
        imuSensor = new CachingSensor<>(() -> imu.getAngularOrientation().firstAngle);
        robot.registerCachingSensor(imuSensor);
    }

    @Override
    public void update(Canvas overlay) {
        telemetryData.addData("motorPosition", motor.getCurrentPosition());
        telemetryData.addData("digitalState", digitalChannel.getState());
        telemetryData.addData("analogVoltage", analogSensor.readRawVoltage());
        telemetryData.addData("heading", imuSensor.getValue());
    }

    public void setMotorVelocity(double velocity) {
        motor.setVelocity(velocity);
    }

    public void servoPositionOne() {
        servo.setPosition(.2);
    }

    public void servoPositionTwo() {
        servo.setPosition(.8);
    }

    public boolean digitalSensorTriggered () {
        return !digitalChannel.getState();
    }
}
