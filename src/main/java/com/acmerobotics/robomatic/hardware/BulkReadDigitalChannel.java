package com.acmerobotics.robomatic.hardware;

import android.util.Log;

import com.acmerobotics.lib.robot.Robot;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.DigitalChannelImpl;

import java.lang.reflect.Field;

public class BulkReadDigitalChannel implements DigitalChannel {

    private Robot robot;
    private  DigitalChannel delegate;
    private DigitalChannelController controller;
    private int channel;

    public BulkReadDigitalChannel (Robot robot, DigitalChannelImpl delegate) {
        this.robot = robot;
        this.delegate = delegate;

        try {
            Field controllerField = DigitalChannelImpl.class.getDeclaredField("controller");
            controllerField.setAccessible(true);
            controller = (DigitalChannelController) controllerField.get(delegate);
            Field channelField = DigitalChannelImpl.class.getDeclaredField("channel");
            controllerField.setAccessible(true);
            channel = channelField.getInt(delegate);
        } catch (Exception e) {
            Log.e(Robot.TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public Mode getMode() {
        return delegate.getMode();
    }

    @Override
    public void setMode(Mode mode) {
        delegate.setMode(mode);
    }

    @Override
    public boolean getState() {
        return robot.getDigitalInput(controller, channel);
    }

    @Override
    public void setState(boolean state) {
        delegate.setState(state);
    }

    @Override
    public void setMode(DigitalChannelController.Mode mode) {
        delegate.setMode(mode);
    }

    @Override
    public Manufacturer getManufacturer() {
        return delegate.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return delegate.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return delegate.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return delegate.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        delegate.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
