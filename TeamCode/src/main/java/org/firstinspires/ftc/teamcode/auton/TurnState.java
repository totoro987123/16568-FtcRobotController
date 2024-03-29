package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Settings;
import org.firstinspires.ftc.teamcode.controllers.IMU;

public class TurnState extends State {

    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;

    private final IMU imu;

    private double gyroTarget;
    private final double gyroRange = 5;
    //private final double minSpeed = 0.15;
    //private final double addSpeed = 0.1;

    private ElapsedTime runtime = new ElapsedTime();
    private int timeout = 5;
    private Telemetry telemetry;

    /**
     * Default state constructor
     * @param hardwareMap
     */
    public TurnState(double gyroTarget, HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap);

        this.imu = IMU.getInstance(IMU.class, hardwareMap);

        this.gyroTarget = gyroTarget; //target angle
        this.telemetry = telemetry;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);
    }

    @Override
    public void start() {
        this.running = true;
        runtime.reset();

        this.imu.initialize();
        this.imu.setDefaultOrientation();

        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);

        //must set the runMode to run without encoder in order for it to run
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        turn(.5);
    }

    @Override
    public void update() {
        if (Math.abs(gyroTarget - imu.getOrientation()) < gyroRange || runtime.seconds() > timeout) { //reached target or too much elapsed time
            this.stop();
            this.goToNextState();
        } else {
            //gyroCorrect(gyroTarget); //re-run method to adjust speed
        }

        telemetry.addLine("Current Angle: " + this.imu.getOrientation());
    }

    @Override
    public void stop() {
        turn(0);
        this.running = false;
    }

    @Override
    public String toString() {
        return "Turn state";
    }

    /**private void gyroCorrect(double gyroTarget) {
        //current heading or angle
        double gyroActual = getCurrentHeading();

        double delta = (gyroTarget - gyroActual + 360.0) % 360.0; // in case it is negative

        if (delta > 180.0) {
            delta -= 360.0; // delta becomes between -180 and 180
            //because the range is from 0-> 180 and -180-> 0 instead of 0-> 360
        }

        if (Math.abs(delta) > gyroRange) {
            double gyroMod = delta / 45.0; // if delta is less than 45 and bigger than -45, this will make a scale from
            // -1 to 1

            if (Math.abs(gyroMod) > 1.0) {
                gyroMod = Math.signum(gyroMod); //makes gyroMod -1 or 1 if error is more than 45
                // or less than -45 degrees
            }

            //if the error is more than 180, then the power is positive, and it turns to the left
            //if the error is less than 180, the power in the turn in negative, and it turns to the
            //right
            //if the error is larger, faster speed
            this.turn(minSpeed * Math.signum(gyroMod) + addSpeed * gyroMod);
        } else {
            turn(0.0);
        }
    }*/

    private void turn(double power) {
        //changed the direction
        fl.setPower(power);
        fr.setPower(-power);
        bl.setPower(power);
        br.setPower(-power);
    }

}