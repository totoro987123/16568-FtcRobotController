package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.controllers.Intake;

public class StartIntakeState extends State {

    private Intake intake;

    public StartIntakeState(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.intake = Intake.getInstance(hardwareMap);
    }

    @Override
    public String toString() {
        return "StartInputState";
    }

    @Override
    public void start() {
        this.intake.enable();
        this.goToNextState();
    }

    @Override
    public void update() {

    }

    @Override
    public void stop() {

    }
}
