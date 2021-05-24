package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Settings;

public class Intake extends Controller {

    // Final Settings
    private final double SPEED = 1;

    // Instance Variables

    private HardwareMap hardwareMap;
    private DcMotor intakeMotor;

    // Constructor
    public Intake(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.intakeMotor = this.hardwareMap.dcMotor.get(Settings.INTAKE);

        this.intakeMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    // Methods

    public void enable() {
        this.intakeMotor.setPower(this.SPEED);
    }

    public void disable() {
        this.intakeMotor.setPower(0);
    }
}
