package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.controllers.Ramp;

public class StartRampState extends State {

    private Ramp ramp;

    public StartRampState(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.ramp = Ramp.getInstance(Ramp.class, hardwareMap);
    }

    @Override
    public String toString() {
        return "StartRampState";
    }

    @Override
    public void start() {
        this.ramp.enable();
        this.goToNextState();
    }

    @Override
    public void update() {

    }

    @Override
    public void stop() {

    }
}
