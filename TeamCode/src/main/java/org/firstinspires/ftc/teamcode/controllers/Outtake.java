package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Settings;

public class Outtake extends Controller  {
    // Final Settings
    private final double SPEED = 1;

    // Instance Variables

    private HardwareMap hardwareMap;

    private DcMotor outtakeMotorLeft;
    private DcMotor outtakeMotorRight;

    private boolean isActive = false;

    // Constructor
    public Outtake(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.outtakeMotorLeft = this.hardwareMap.dcMotor.get(Settings.OUTTAKE_LEFT);
        this.outtakeMotorRight = this.hardwareMap.dcMotor.get(Settings.OUTTAKE_RIGHT);

        this.outtakeMotorLeft.setDirection(DcMotor.Direction.FORWARD);
        this.outtakeMotorLeft.setDirection(DcMotor.Direction.REVERSE);

    }

    // Methods

    public void enable() {
        this.isActive = true;

        this.outtakeMotorLeft.setPower(this.SPEED);
        this.outtakeMotorRight.setPower(this.SPEED);
    }

    public void disable() {
        this.isActive = false;

        this.outtakeMotorLeft.setPower(0);
        this.outtakeMotorRight.setPower(0);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void toggle() {
        if (this.isActive) {
            this.disable();
            return;
        }

        this.enable();
    }
}
