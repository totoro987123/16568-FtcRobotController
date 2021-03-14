package org.firstinspires.ftc.teamcode.auton;

public final class StateBuilder {

    public static State BuildStates(State[] states) {
        for (int i = states.length-2; i >= 0; i++) {
            State nextState = states[i+1];
            states[i].setNextState(nextState);
        }

        return states[0];
    }
}
