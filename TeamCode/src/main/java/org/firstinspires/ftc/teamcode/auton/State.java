package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.framework.annotations.Loadable;

/**
 * This is the state abstract class with defines the functionality of a state.
 * @author Lawson Wright, Brandon Pae
 * @version 1.0
 * @since March 8, 2021
 */
public abstract class State {

    // INSTANCE VARIABLES

    /**
     * Hardware map instance.
     */
    protected HardwareMap hardwareMap;


    /**
     * The state to start after this one concludes.
     */
    protected State nextState;

    /**
     * Represents if the state is running or not.
     */
    protected boolean running = false;


    // CONSTRUCTORS

    /**
     * Default state constructor
     */
    public State(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    // METHODS

    /**
     * Stops this state and starts the next one.
     */
    protected void goToNextState() {
        this.stop();

        if (this.nextState != null) {
            this.nextState.start();
        }
    }

    /**
     * Returns the current running state through recursion.
     * @return
     */
    public State getCurrentState() {
        if ((!this.running) && (this.nextState == null)) {
            return null;
        } else if (!this.running) {
            return this.nextState.getCurrentState();
        }

        return this;
    }



    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    // ABSTRACT METHODS

    /**
     * Gets the State as a string.
     * @return the String representation of the state.
     */
    public abstract String toString();
    /**
     * Starts the state.
     */
    public abstract void start();

    /**
     * Updates the state.
     */
    public abstract void update();

    /**
     * Stops the state.
     */
    public abstract void stop();
}
