package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Settings;

public class PIDController {

    private double kp = 1.5;
    private double kd = 0.6;
    private double ki = 0.01;

    private double error = 0;
    private double prev_error;
    private double diff_error;
    private double sum_error;

    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;

    private int targetPosition;

    public PIDController(double Kp, double Ki, double Kd, HardwareMap hardwareMap, int position) {
        kp = Kp;
        ki = Ki;
        kd = Kd;

        targetPosition = position;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        fl.setDirection(DcMotor.Direction.FORWARD);
        //fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);

        //double elapsedTime = 1.0;
    }

    protected void PIDControl() {
        prev_error = error;
        //get the error, or the distance from the target
        double averageCurrentPosition = (fl.getCurrentPosition() + fr.getCurrentPosition() + bl.getCurrentPosition()
                + br.getCurrentPosition()) / 3.0;

        error = Math.abs(Math.abs(averageCurrentPosition) - (Math.abs(targetPosition)));

        //to make the error fit within the range of 0-1, we can divide it by the original targetPosition
        //i.e if the error is like 1600 in the beginning, then obviously the target position is 1600
        //but as error gets smaller, gets closer

        //scale error

        error /= targetPosition; //scales it, halfway becomes error of 0.5
        //scales from 1 to 0

        error = 1 - error; //if its 1, becomes 0, if its 0, becomes 1. this is so that I can properly
        //scale the error along a sinusodial function (as x increases)

        // new function scales error at the start from 0.4 to low of 0.1
        /// since error correlates with power, this will ensure a low power at the beginning,
        // increased power towards the middle, and low power towards the end (but not 0)
        //
        error = 0.9 * Math.sin ((Math.PI / 1.1) * error + 0.24) + 0.1;

        //one error we end up having is that scaling it like above, from 1 to 0, doesn't work very well
        //as it approaches 0, it takes too long, so we need a sinusoidal function
        //bot also immediately has set power of 0.7 at start, we should start slow and gear up for
        //more precise movement, as shown by the sine function

        //diff_error = (error - prev_error) / elapsedTime; //difference in the errors for the kd constant,
        diff_error = (error - prev_error);
        //and divided by elapsedTime to find the change in error

        sum_error += error; //add the error to the total number of errors

        //since error is too large in this case
        double driveSpeed = kp * error + ki * sum_error + kd * diff_error;

        fl.setPower(driveSpeed);
        fr.setPower(driveSpeed);
        bl.setPower(driveSpeed);
        br.setPower(driveSpeed);
    }

}
