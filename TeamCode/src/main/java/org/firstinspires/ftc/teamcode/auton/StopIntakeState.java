package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.controllers.Intake;

public class StopIntakeState extends State {

    private Intake intake;

    public StopIntakeState(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.intake = Intake.getInstance(hardwareMap);
    }

    @Override
    public String toString() {
        return "StopInputState";
    }

    @Override
    public void start() {
        this.intake.disable();
        this.goToNextState();
    }

    @Override
    public void update() {

    }

    @Override
    public void stop() {

    }
}
