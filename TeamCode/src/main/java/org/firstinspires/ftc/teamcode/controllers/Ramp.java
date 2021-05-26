package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Settings;

public class Ramp extends Controller {
    // Final Settings
    private final double MAX_SPEED = .33;

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

    public void setPower(final double power) {
        this.rampMotor.setPower(power);
    }

    public void enable() {
        this.setPower(this.MAX_SPEED);
    }

    public void disable() {
        this.rampMotor.setPower(0);
    }

    public void setPowerPercent(float Percent) {
        this.setPower(MAX_SPEED * Percent);
    }
}
