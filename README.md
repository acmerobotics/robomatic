# Installing
## Add the submodule

From the root of the project (e.g. path/to/projects/skystone) run `git submodule add https://github.com/acmerobotics/robomatic.git`

In the future after cloning the project new users will need to run `git submodule init` and `git submodule update` to populate the submodule files into the Robomatic folder created by cloning the main project.

## Installation

* In **settings.gradle** for the project add `include ':robomatic'`
* In the **build.release.gradle** file in both the FtcRobotController and TeamCode modules, add the line `implementation project(':robomatic')` to the end of the dependencies. 
* Sync gradle when prompted.
* Follow the instructions to install the dashboard. 
  * In step three of the dashboard installation instructions make sure to add the dependency to the Robomatic module. 
  * During step 8 of the dashboard installation instructions:
    * In the first location where you are told to add lines also add: `OpModeConfigurationActivity.populateMenu(popupMenu.getMenu(), FtcRobotControllerActivity.this);`
    * In the second location also add: `OpModeConfigurationActivity.populateMenu(menu, this);`

## Java 8

Robomatic makes use of lambdas which require Java 8. Java 8 has been supported in Android Studio since 3.0 but the source and target compatability for Java 8 must be specified in the project build files. The Robomatic gradle file contains
the section: 
```
   compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
```
which indicates to gradle that it uses Java 8. This may be sufficient to work with your project. However, if you get build errors from this you may need to include the same section in the `build.gradle` file for the TeamCode module. Add this section:
```
android {
     compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
**after** the `apply from: '../build.common.gradle'` line so it overwrites the preference set there. Alternatively, if you want all of your modules to support using Java 8 features add that section to the `build.common.gradle` file in the main SkyStone project, replacing the current source and target compatibility indicator of Java 1.7. To our knowledge, this does not cause any issues with Java 7 code or code for older API levels as the Android tools convert the byte code to be supported by the older API level through the use of their "desugaring" tools. 

# Usage
## Robots

The Robot class controls the main loop that provides efficient asynchronous control of hardware. 
Hardware devices and CachingSensors accessed through the Robot will only communicate with the Rev 
hubs once per loop, and only write data if they need to. Each iteration of the main loop the Robot 
will: 
1. update bulk data from the rev hubs 
2. update caching sensors 
3. call each of the subsystem's update method 
4. update dashboard telemetry
5. write any changes to motor or servo commands to the rev hubs

## Usage

To make your own robot, extend the Robot class. The robot object will contain all the subsystems and interface with all the hardware.

Enable devices connected to a Rev Hub by registering the hub with the robot by puting the line `registerHub("<configured name>")` in the constructor of your robot class.
 
After initializing the subsystem in your robot's constructor register the subsystem with `registerSubsystem(<subsystem>);` Pass the robot instance into your subsystems so they can interact with the robot loop and hardware.

## Accessing Hardware

To use the caching hardware devices provided by the Robot, access hardware from the robot rather than directly from the hardware map. The methods `getMotor()`, `getServo()`, `getDigitalChannel()`, and `getAnalogSensor()` take a name a device is registered with in the hardware map, and return a hardware device that is managed by the Robot.

To minimize your own expensive sensor reads (for example reading an imu's orientation or other i2c devices) wrap the expensive call in a CachingSensor object, which takes the call as its sole argument, and then register the sensor with the Robot using robot.registerCachingSensor. The lambda will be evaluated each loop, and the return value of the function is accesible through the CachingSensor's `getValue()` method. To improve loop times, disable the sensor when not in use by calling `setEnabled(false)`. The sensor can be re-enabled when it is needed again.

To create an imu, call the robot's `getRevHubImu()` method, and pass the index of the hub from which to retrieve the IMU **NOTE: index here refers to the order in which the hubs were registered with the robot, not the addresses of the hubs**. To perform an optional axis remap pass a `Robot.Orientation` object as the second argument. This orientation defines the directions of the positive x, y, and z axes in the imu's origional refrence frame. Refer to Bosch's documentation for details.

## Updating the robot

`Robot.update()` will preform one update of the robot. If you have your own main loop, such as a loop in teleop that updates gamepad controls and such, call `Robot.update()` once each loop.

The robot can also run its own main loop, updating itself until one of several conditions are met:
* `runUntilStop` will run the main loop until the opmode is stopped.
* `runForTime` will run the main loop for a specified number of milliseconds. This is useful if you want a delay between actions in auto, but would like the loop to continue to run.
* `runUntil` accepts a lambda target. The loop will be run until the lambda returns true.
