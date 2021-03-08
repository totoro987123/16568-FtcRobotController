package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


@Autonomous(name = "AutonStateMachine")
public class AutonStateMachine extends OpMode{

    private final double VERSION = 1.0;
    private State headerState;

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        State currentState = headerState.getCurrentState();
        currentState.update();

        telemetry.addLine("Version: " + this.VERSION);

        telemetry.update();
    }

    @Override
    public void stop() {

    }
}