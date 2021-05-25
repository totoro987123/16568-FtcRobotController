package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Settings;

public class Ramp extends Controller {
    // Final Settings
    private final double SPEED = .33;

    // Instance Variables

    private HardwareMap hardwareMap;
    private DcMotor rampMotor;

    // Constructor
    public Ramp(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.rampMotor = this.hardwareMap.dcMotor.get(Settings.RAMP);

        this.rampMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    // Methods

    public void enable() {
        this.rampMotor.setPower(this.SPEED);
    }

    public void disable() {
        this.rampMotor.setPower(0);
    }
}
