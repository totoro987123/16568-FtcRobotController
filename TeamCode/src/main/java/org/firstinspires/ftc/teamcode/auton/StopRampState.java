package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.controllers.Ramp;

public class StopRampState extends State {

    private Ramp ramp;

    public StopRampState(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.ramp = Ramp.getInstance(Ramp.class, hardwareMap);
    }

    @Override
    public String toString() {
        return "StartInputState";
    }

    @Override
    public void start() {
        this.ramp.disable();
        this.goToNextState();
    }

    // T
    @Override
    public void update() {

    }

    @Override
    public void stop() {

    }
}
