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
import com.acmerobotics.robomatic.hardware.LynxOptimizedI2cFactory;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.lynx.LynxEmbeddedIMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.DigitalChannelImpl;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Robot {

    public static final String TAG = "Robot";

    private HardwareMap map;
    private LinearOpMode opmode;

    private List<Subsystem> subsystems;

    private Map<DcMotorController, LynxModule> motorControllers;
    private Map<AnalogInputController, LynxModule> analogInputControllers;
    private Map<DigitalChannelController, LynxModule> digitalChannelControllers;

    private Map<LynxModule, LynxGetBulkInputDataResponse> bulkDataResponses;
    private List<LynxModule> hubs;

    private boolean bulkDataUpdated = false;

    private List<CachingHardwareDevice> cachingHardwareDevices;
    private List<CachingHardwareDevice> cachingSensors;

    private Map<String, Object> telemetry;
    private List<String> telemetryLines;

    public Robot (LinearOpMode opmode) {
        this.map = opmode.hardwareMap;
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
        for (LynxModule hub : hubs) {
            LynxGetBulkInputDataCommand command = new LynxGetBulkInputDataCommand(hub);
            try {
                bulkDataResponses.put(hub, command.sendReceive());
                bulkDataUpdated = true;
            } catch (Exception e) {
                try {
                    Log.e(TAG,  "get bulk data error");
                    Log.e(TAG, e.getLocalizedMessage());
                } catch (NullPointerException npe) {
                    Log.e(TAG, "error logging exception");
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
        for (Subsystem subsystem: subsystems) {
            subsystem.update(packet.fieldOverlay());
            packet.putAll(subsystem.telemetryData.getData());
        }
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    public void update () {
        updateBulkData();
        updateCachingSensors();
        updateSubsystems();
        updateCachingHardwareDevices();
    }

    public interface Target {
        boolean reached();
    }

    public void runUntil(Target target) {
        while (!opmode.isStopRequested() && !target.reached()) {
            update();
        }
    }

    public void runUntilStop() {
        runUntil(() -> false);
    }

    public void runForTime(long millis) {
        long end = System.currentTimeMillis() + millis;
        runUntil(() -> System.currentTimeMillis() >= end);
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
        LynxModule hub = map.get(LynxModule.class, deviceName);
        hubs.add(hub);
        motorControllers.put(map.get(DcMotorController.class, deviceName), hub);
        analogInputControllers.put(map.get(AnalogInputController.class, deviceName), hub);
        digitalChannelControllers.put(map.get(DigitalChannelController.class, deviceName), hub);
    }

    protected void registerSubsytem (Subsystem subsystem) {
        if (!subsystems.contains(subsystem)) subsystems.add(subsystem);
    }

    public void registerCachingSensor (CachingSensor sensor) {
        if (!cachingSensors.contains(sensor)) cachingSensors.add(sensor);
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

    public enum Axis {
        POSITIVE_X {
            @Override
            public byte SIGN() {
                return 0b00;
            }

            @Override
            public byte AXIS() {
                return 0b00;
            }
        },
        POSITIVE_Y {
            @Override
            public byte SIGN() {
                return 0;
            }

            @Override
            public byte AXIS() {
                return 0b01;
            }
        },
        POSITIVE_Z {
            @Override
            public byte SIGN() {
                return 0;
            }

            @Override
            public byte AXIS() {
                return 0b10;
            }
        },
        NEGATIVE_X {
            @Override
            public byte SIGN() {
                return 1;
            }

            @Override
            public byte AXIS() {
                return 0b00;
            }
        },
        NEGATIVE_Y {
            @Override
            public byte SIGN() {
                return 1;
            }

            @Override
            public byte AXIS() {
                return 0b01;
            }
        },

        NEGATIVE_Z {
            @Override
            public byte SIGN() {
                return 1;
            }

            @Override
            public byte AXIS() {
                return 0b10;
            }
        };

        public abstract byte SIGN();
        public abstract byte AXIS();
    }

    public static class Orientation {
        private final  Axis X, Y, Z;

        public Orientation(Axis x, Axis y, Axis z) {
            X = x;
            Y = y;
            Z = z;
        }
    }

    public BNO055IMUImpl getRevHubImu(int hub) {
        I2cDeviceSynch imuI2cDevice = LynxOptimizedI2cFactory.createLynxI2cDeviceSynch(hubs.get(hub), 0);
        // todo is this necessary
//        imuI2cDevice.setUserConfiguredName("imu");
        LynxEmbeddedIMU imu = new LynxEmbeddedIMU(imuI2cDevice);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);
        return imu;
    }

    public BNO055IMUImpl getRevHubImu(int hub, Orientation orientation) {
        BNO055IMUImpl imu = getRevHubImu(hub);

        try {
            // axis remap
            byte x_OFFSET = 0;
            byte y_OFFSET = 2;
            byte z_OFFSET = 4;
            int AXIS_MAP_CONFIG_BYTE = orientation.X.AXIS() << x_OFFSET | orientation.Y.AXIS() << y_OFFSET | orientation.Z.AXIS() << z_OFFSET; //swaps y-z, 0b00100001 is y-x, 0x6 is x-z

            byte y_SIGN_OFFSET = 1;
            byte x_SIGN_OFFSET = 2;
            byte z_SIGN_OFFSET = 0;
            int AXIS_MAP_SIGN_BYTE = orientation.X.SIGN() << x_SIGN_OFFSET | orientation.Y.SIGN() << y_SIGN_OFFSET | orientation.Z.SIGN() << z_SIGN_OFFSET; //x, y, z

            //Need to be in CONFIG mode to write to registers
            imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.CONFIG.bVal & 0x0F);

            Thread.sleep(100); //Changing modes requires a delay before doing anything else

            //Write to the AXIS_MAP_CONFIG register
            imu.write8(BNO055IMU.Register.AXIS_MAP_CONFIG, AXIS_MAP_CONFIG_BYTE & 0x0F);

            //Write to the AXIS_MAP_SIGN register
            imu.write8(BNO055IMU.Register.AXIS_MAP_SIGN, AXIS_MAP_SIGN_BYTE & 0x0F);

            //Need to change back into the IMU mode to use the gyro
            imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.IMU.bVal & 0x0F);

            Thread.sleep(100); //Changing modes again requires a delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return imu;
    }

}
