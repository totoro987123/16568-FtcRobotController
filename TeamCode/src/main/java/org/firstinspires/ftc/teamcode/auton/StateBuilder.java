package org.firstinspires.ftc.teamcode.auton;

import org.firstinspires.ftc.teamcode.framework.annotations.Service;

public final class StateBuilder {

    public static State buildStates(State[] stateSequence) {
        for (int i = stateSequence.length-2; i >= 0; i--) {
            State nextState = stateSequence[i+1];
            stateSequence[i].setNextState(nextState);
        }

        return stateSequence[0];
    }
}
