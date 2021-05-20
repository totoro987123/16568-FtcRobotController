package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.framework.StandardOpMode;

@Autonomous(name = "AutonFrameworkTest")
public class TestAutonFramework extends StandardOpMode {

    // INSTANCE VARIABLES
    /**
     * Version of the op-mode file.
     */
    private final double VERSION = 1.0;

    public TestAutonFramework() {
        super(TestAutonFramework.class);
    }

    // METHODS


    /**
     * Sets up all relevant things for the op-mode.
     */
    @Override
    public void init() {
        this.initFramework();
    }

    /**
     * Runs all things related to starting the op-mode.
     */
    @Override
    public void start() {

    }

    @Override
    public void loop() {
        for (Object object : this.loadables.values()) {
            this.telemetry.addLine("Library: " + object.getClass().getName());
        }

        this.telemetry.addLine("Version: " + this.VERSION);
    }

    @Override
    public void stop() {
    }
}
