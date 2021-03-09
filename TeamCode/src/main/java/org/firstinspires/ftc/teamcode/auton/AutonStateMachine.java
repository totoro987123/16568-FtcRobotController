package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


@Autonomous(name = "AutonStateMachine")
public class AutonStateMachine extends OpMode{

    // INSTANCE VARIABLES
    /**
     * Version of the op-mode file.
     */
    private final double VERSION = 1.0;

    /**
     * The first state to be run.
     */
    private State headerState;

    // METHODS

    /**
     * Sets up all relevant things for the op-mode.
     */
    @Override
    public void init() {
        headerState = new DriveState(10, 0.7, hardwareMap);
    }

    /**
     * Runs all things related to starting the op-mode.
     */
    @Override
    public void start() {
        this.headerState.start();
    }

    @Override
    public void loop() {
        State currentState = headerState.getCurrentState();
        currentState.update();

        telemetry.addLine("Version: " + this.VERSION);
        telemetry.addLine("CurrentState: " + currentState.toString());

        telemetry.update();
    }

    @Override
    public void stop() {
        State currentState = headerState.getCurrentState();
        currentState.stop();
    }
}