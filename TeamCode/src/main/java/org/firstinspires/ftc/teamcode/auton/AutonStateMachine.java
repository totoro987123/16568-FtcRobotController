package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.controllers.IMU;

@Autonomous(name = "AutonStateMachine")
public class AutonStateMachine extends OpMode {

    // INSTANCE VARIABLES
    /**
     * Version of the op-mode file.
     */
    private final double VERSION = 1.0;

    /**
     * The first state to be run.
     */
    private State headerState;

    private IMU imu;

    // METHODS


    /**
     * Sets up all relevant things for the op-mode.
     */
    @Override
    public void init() {
        State[] defaultStateSequence = {
                new StartRampState(hardwareMap),
                new DriveState(10, 0.8, "front", hardwareMap, telemetry),
                new TurnState(90, hardwareMap, telemetry),
                new StopRampState(hardwareMap),
                new StartIntakeState(hardwareMap),
                new DriveState(10, 0.8, "front", hardwareMap, telemetry),
                new TurnState(-180, hardwareMap, telemetry),
                new StopIntakeState(hardwareMap),

                //new StartIntakeState(hardwareMap),
                //new DriveState(10, 0.8, "front", hardwareMap, telemetry),
                //new StopIntakeState(hardwareMap),
                //new StartRampState(hardwareMap),
                //new DriveState(10, 0.8, "left", hardwareMap, telemetry),
                //new StopRampState(hardwareMap)
                //new StrafeState(12, 0.7, hardwareMap, "back", telemetry),
                //new StrafeState(15, hardwareMap, "left", telemetry)
                //new TurnState(90, hardwareMap),
                //new TurnState(-45, hardwareMap)
        };

        this.imu = IMU.getInstance(IMU.class, hardwareMap);

        headerState = StateBuilder.buildStates(defaultStateSequence);
    }

    /**
     * Runs all things related to starting the op-mode.
     */
    @Override
    public void start() {
        this.imu.setDefaultOrientation();
        this.headerState.start();
    }

    @Override
    public void loop() {
        State currentState = headerState.getCurrentState();
        boolean running = currentState != null;

        String status = running ? "RUNNING" : "COMPLETED";
        String currentStateString = running ? currentState.toString() : "None";

        // State telemetry
        telemetry.addLine("CurrentState: " + currentStateString);
        telemetry.addLine("Status: " + status);
        telemetry.addLine("Orientation: " + this.imu.getOrientation());

        // Update State
        if (running) {
            currentState.update();
        }

        // Version telemetry.
        telemetry.addLine("Version: " + this.VERSION);
    }

    @Override
    public void stop() {
        State currentState = headerState.getCurrentState();

        if (currentState != null) {
            currentState.stop();
        }

        this.imu.close();
    }
}