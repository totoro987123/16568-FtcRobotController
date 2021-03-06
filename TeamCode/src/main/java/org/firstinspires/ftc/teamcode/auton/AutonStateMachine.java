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
        boolean running = currentState != null;

        if (running) {
            currentState.update();
        }

        String status = running ? "RUNNING" : "COMPLETED";
        String currentStateString = running ? currentState.toString() : "None";

        telemetry.addLine("CurrentState: " + currentStateString);
        telemetry.addLine("Status: " + status);

        telemetry.addLine("Version: " + this.VERSION);
        telemetry.update();
    }

    @Override
    public void stop() {
        State currentState = headerState.getCurrentState();
        currentState.stop();
    }
}