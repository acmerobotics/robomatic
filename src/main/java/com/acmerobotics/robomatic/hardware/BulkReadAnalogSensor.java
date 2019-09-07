package com.acmerobotics.robomatic.hardware;

import android.util.Log;

import com.acmerobotics.robomatic.robot.Robot;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogSensor;

import java.lang.reflect.Field;

public class BulkReadAnalogSensor implements AnalogSensor {

    private Robot robot;
    private AnalogInputController controller;
    private int port;

    public BulkReadAnalogSensor (Robot robot, AnalogInput delegate) {
        this.robot = robot;

        try {
            Field controllerField = AnalogInput.class.getDeclaredField("controller");
            controllerField.setAccessible(true);
            controller = (AnalogInputController) controllerField.get(delegate);
            Field channelField = AnalogInput.class.getDeclaredField("channel");
            controllerField.setAccessible(true);
            port = channelField.getInt(delegate);
        } catch (Exception e) {
            Log.e(Robot.TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public double readRawVoltage() {
        return robot.getAnalogInput(controller, port);
    }
}
