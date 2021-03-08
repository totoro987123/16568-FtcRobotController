package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class State {

    // INSTANCE VARIABLES
    private HardwareMap hardwareMap;
    private State nextState;
    private Boolean running = false;

    // CONSTRUCTORS
    public State(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    // METHODS
    public void goToNextState() {
        this.stop();

        this.nextState.start();
    }

    public State getCurrentState() {
        if (!this.running) {
            return this.nextState.getCurrentState();
        }

        return this;
    }

    // ABSTRACT METHODS
    public abstract void start();
    public abstract void update();
    public abstract void stop();
}
