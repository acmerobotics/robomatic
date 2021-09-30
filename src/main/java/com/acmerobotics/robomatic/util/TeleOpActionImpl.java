package com.acmerobotics.robomatic.util;

import com.acmerobotics.robomatic.util.StickyGamepad;
import com.qualcomm.robotcore.hardware.Gamepad;

public interface TeleOpActionImpl {
    void action(Gamepad gamepad1, Gamepad gamepad2, StickyGamepad stickyGamepad1, StickyGamepad stickyGamepad2);
}
