package com.acmerobotics.robomatic.robot;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.robomatic.hardware.BulkReadAnalogSensor;
import com.acmerobotics.robomatic.hardware.BulkReadDigitalChannel;
import com.acmerobotics.robomatic.hardware.CachingDcMotorEx;
import com.acmerobotics.robomatic.hardware.CachingHardwareDevice;
import com.acmerobotics.robomatic.hardware.CachingSensor;
import com.acmerobotics.robomatic.hardware.CachingServo;
import com.qualcomm.hardware.lynx.LynxModuleIntf;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.DigitalChannelImpl;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Robot {

    public static final String TAG = "Robot";

    private HardwareMap map;
    private ScheduledOpMode opmode;

    private List<Subsystem> subsystems;

    private Map<DcMotorController, LynxModuleIntf> motorControllers;
    private Map<AnalogInputController, LynxModuleIntf> analogInputControllers;
    private Map<DigitalChannelController, LynxModuleIntf> digitalChannelControllers;

    private Map<LynxModuleIntf, LynxGetBulkInputDataResponse> bulkDataResponses;
    private List<LynxModuleIntf> hubs;

    private boolean bulkDataUpdated = false;

    private List<CachingHardwareDevice> cachingHardwareDevices;
    private List<CachingHardwareDevice> cachingSensors;

    private Map<String, Object> telemetry;
    private List<String> telemetryLines;

    public Robot (ScheduledOpMode opmode, HardwareMap map) {
        this.map = map;
        this.opmode = opmode;

        motorControllers = new HashMap<>(2);
        analogInputControllers = new HashMap<>(2);
        digitalChannelControllers = new HashMap<>(2);

        bulkDataResponses = new HashMap<>(2);
        hubs = new ArrayList<>(2);

        cachingHardwareDevices = new ArrayList<>();
        cachingSensors = new ArrayList<>();

        telemetry = new HashMap<>();
        telemetryLines = new ArrayList<>();

        subsystems = new ArrayList<>();

    }

    public void addTelemetry (String caption, Object value) {
        telemetry.put(caption, value);
    }

    public void addTelemetryLine (String line) {
        if (!telemetryLines.contains(line)) telemetryLines.add(line);
    }

    public void clearTelemetry () {
        telemetryLines.clear();
        telemetry.clear();
    }

    private void updateBulkData () {
        for (LynxModuleIntf hub : hubs) {
            LynxGetBulkInputDataCommand command = new LynxGetBulkInputDataCommand(hub);
            try {
                bulkDataResponses.put(hub, command.sendReceive());
                bulkDataUpdated = true;
            } catch (Exception e) {
                try {
                    Log.e(TAG,  "get bulk data error");
                    Log.e(TAG, e.getLocalizedMessage());
                } catch (NullPointerException npe) {
                    Log.e(TAG, "this is really funny");
                }
                bulkDataUpdated = false;
            }
        }

    }

    private void updateCachingHardwareDevices () {
        for (CachingHardwareDevice device: cachingHardwareDevices) device.update();
    }

    private void updateCachingSensors () {
        for (CachingHardwareDevice device: cachingSensors) device.update();
    }

    private void updateSubsystems () {
        TelemetryPacket packet = new TelemetryPacket();
        packet.putAll(telemetry);
        for (String line: telemetryLines) packet.addLine(line);
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    public void update () {
        updateBulkData();
        updateCachingSensors();
        updateSubsystems();
        updateCachingHardwareDevices();
    }

    public DcMotorEx getMotor (String deviceName) {
        CachingDcMotorEx motor =  new CachingDcMotorEx(this, map.get(DcMotorEx.class, deviceName));
        cachingHardwareDevices.add(motor);
        return motor;
    }

    public Servo getServo (String deviceName) {
        CachingServo servo = new CachingServo( map.get(Servo.class, deviceName));
        cachingHardwareDevices.add(servo);
        return servo;
    }

    public AnalogSensor getAnalogSensor (String deviceName) {
        return new BulkReadAnalogSensor(this, map.get(AnalogInput.class, deviceName));
    }

    public DigitalChannel getDigitalChannel (String deviceName) {
        return new BulkReadDigitalChannel(this, map.get(DigitalChannelImpl.class, deviceName));
    }

    protected void registerHub (String deviceName) {
        LynxModuleIntf hub = map.get(LynxModuleIntf.class, deviceName);
        hubs.add(hub);
        motorControllers.put(map.get(DcMotorController.class, deviceName), hub);
        analogInputControllers.put(map.get(AnalogInputController.class, deviceName), hub);
        digitalChannelControllers.put(map.get(DigitalChannelController.class, deviceName), hub);
    }

    protected void registerSubsytem (Subsystem subsystem) {
        if (!subsystems.contains(subsystem)) subsystems.add(subsystem);
    }

    public void registerCachingSensor (CachingSensor sensor) {
        cachingSensors.add(sensor);
    }

    private LynxGetBulkInputDataResponse getBulkResponse (DcMotorController controller) {
        return bulkDataResponses.get(motorControllers.get(controller));
    }

    private LynxGetBulkInputDataResponse getBulkResponse (AnalogInputController controller) {
        return bulkDataResponses.get(analogInputControllers.get(controller));
    }

    private LynxGetBulkInputDataResponse getBulkResponse (DigitalChannelController controller) {
        return bulkDataResponses.get(digitalChannelControllers.get(controller));
    }

    public int getEncoder (DcMotorController controller, int channel) {
        if (!bulkDataUpdated) {
            updateBulkData();
        }
        return getBulkResponse(controller).getEncoder(channel);
    }

    public int getAnalogInput (AnalogInputController controller, int port) {
        if (!bulkDataUpdated) return 0;
        return getBulkResponse(controller).getAnalogInput(port);
    }

    public boolean getDigitalInput (DigitalChannelController channelController, int port) {
        if (!bulkDataUpdated) return false;
        return getBulkResponse(channelController).getDigitalInput(port);
    }

    public double getVelocity (DcMotorController controller, int port) {
        if (!bulkDataUpdated) return 0;
        return getBulkResponse(controller).getVelocity(port);
    }

}
