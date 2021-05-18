package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Ramp {

    // Static Variables
    private static Ramp ramp = null;

    // Final Settings
    private final double SPEED = 1;

    // Instance Variables

    private HardwareMap hardwareMap;
    private DcMotor rampMotor;

    // Constructor
    public Ramp(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.rampMotor = this.hardwareMap.dcMotor.get("ramp");

        this.rampMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    // Methods

    public void enable() {
        this.rampMotor.setPower(this.SPEED);
    }

    public void disable() {
        this.rampMotor.setPower(0);
    }

    // Static Methods
    public static Ramp getInstance(HardwareMap hardwareMap) {
        if (ramp == null) {
            ramp = new Ramp(hardwareMap);
        }

        return ramp;
    }
}
