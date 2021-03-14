package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Settings;

public class TurnState extends State {

    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters;
    private float lastAngle = 0;
    private double gyroTarget;
    private final double gyroRange = 0.5;
    private final double minSpeed = 0.2;
    private final double addSpeed = 0.1;
    private AngleUnit unit = AngleUnit.DEGREES;
    private ElapsedTime runtime = new ElapsedTime();
    private int timeout = 5;

    /**
     * Default state constructor
     *
     * @param hardwareMap
     */
    public TurnState(double gyroTarget, HardwareMap hardwareMap) {
        super(hardwareMap);

        this.running = true;
        this.gyroTarget = gyroTarget; //target angle

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        /**
         fl.setDirection(DcMotor.Direction.FORWARD);
         fr.setDirection(DcMotor.Direction.REVERSE);
         bl.setDirection(DcMotor.Direction.FORWARD);
         br.setDirection(DcMotor.Direction.REVERSE);
         */

        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);

        parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);
    }

    @Override
    public void start() {
        runtime.reset();
        lastAngle = getGyroRotation(unit); //get the initial angle, relative to beginning configuration
        gyroCorrect(gyroTarget);
    }

    @Override
    public void update() {
        if (Math.abs(gyroTarget - getCurrentHeading()) < 1.5 || runtime.seconds() > timeout) { //reached target or too much elapsed time
            this.stop();
            this.goToNextState();
        }
        else {
            gyroCorrect(gyroTarget); //re-run method to adjust speed
        }
    }

    @Override
    public void stop() {
        turn(0);
        this.running = false;
    }

    @Override
    public String toString() {
        return null;
    }

    private void gyroCorrect(double gyroTarget) {
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
    }

    private double getCurrentHeading() { //the relative angle, which is after the heading becomes 0
        return getGyroRotation(unit) - lastAngle;
    }

    private void turn(double power) {
        //must set the runMode to run without encoder in order for it to run
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //changed the direction
        fl.setPower(power);
        fr.setPower(-power);
        bl.setPower(power);
        br.setPower(-power);
    }

    //returns current angle, relative to initial initialization angle (not relative to last position)
    private float getGyroRotation(AngleUnit unit) {
        //first angle = the x-coordinate, or heading
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, unit).firstAngle;
    }
}