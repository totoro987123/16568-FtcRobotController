package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    // Static Variables
    private static Intake intake = null;

    // Final Settings
    private final double SPEED = 1;

    // Instance Variables

    private HardwareMap hardwareMap;
    private DcMotor leftIntake;
    private DcMotor rightIntake;

    // Constructor
    public Intake(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.leftIntake = this.hardwareMap.dcMotor.get("left_intake");
        this.rightIntake = this.hardwareMap.dcMotor.get("right_intake");

        this.leftIntake.setDirection(DcMotor.Direction.FORWARD);
        this.rightIntake.setDirection(DcMotor.Direction.REVERSE);
    }

    // Methods

    public void enable() {
        this.leftIntake.setPower(this.SPEED);
        this.rightIntake.setPower(this.SPEED);
    }

    public void disable() {
        this.leftIntake.setPower(0);
        this.rightIntake.setPower(0);
    }

    // Static Methods
    public static Intake getInstance(HardwareMap hardwareMap) {
        if (intake == null) {
            intake = new Intake(hardwareMap);
        }

        return intake;
    }
}
