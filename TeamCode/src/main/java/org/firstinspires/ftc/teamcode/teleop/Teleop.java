package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Settings;

@TeleOp(name="Teleop", group="Iterative Opmode")

public class Teleop extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Drive
    private DcMotor fl = null;
    private DcMotor fr = null;
    private DcMotor bl = null;
    private DcMotor br = null;

    //SpeedFactor
    private double speedFactor = 1;
    private boolean lastPressed = false;

    private int CurrentGamepad = 1;

    private boolean strafing = false;
    private double strafeStartingAngle = 0;
    private BNO055IMU imu;
    private Orientation angles;
    private BNO055IMU.Parameters parameters;
    private int encoderSum = 0;

    private int holdPos = 0;

    @Override
    public void init() {

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        runtime.reset();
    }

    public double compareAngles(AngleUnit angleUnit, double angle) {
        double degrees = AngleUnit.DEGREES.fromUnit(angleUnit, angle);

        return AngleUnit.DEGREES.normalize(degrees);
    }

    @Override
    public void loop() {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        // Setup a variable for each drive wheel to save power level for telemetry

        //Set power for wheels based on math

        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        if (Math.abs(gamepad1.left_stick_x) >= .1){
            if (!strafing) {
                strafing = true;
                //encoderSum = fl.getCurrentPosition()+fr.getCurrentPosition()+bl.getCurrentPosition()+br.getCurrentPosition();
                encoderSum = fl.getCurrentPosition()+bl.getCurrentPosition()+br.getCurrentPosition();
                strafeStartingAngle = compareAngles(angles.angleUnit, angles.firstAngle);
            }
        } else {
            strafing = false;
        }
        double v1 = 0;
        double v2 = 0;
        double v3 = 0;
        double v4 = 0;

        double v1correction = 0;
        double v2correction = 0;
        double v3correction = 0;
        double v4correction = 0;
        if (strafing){
            double increment = -.25;
            int acceptableRange = 3;
            int range = 500;
            //int newSum = fl.getCurrentPosition()+fr.getCurrentPosition()+bl.getCurrentPosition()+br.getCurrentPosition();
            int newSum = fl.getCurrentPosition()+bl.getCurrentPosition()+br.getCurrentPosition();
            if (newSum-range > encoderSum){
                v1correction = -increment;
                v2correction = -increment;
                v3correction= -increment;
                v4correction = -increment;
            } else if (newSum+range < encoderSum) {
                v1correction = increment;
                v2correction = increment;
                v3correction = increment;
                v4correction = increment;
            } else if ((compareAngles(angles.angleUnit, angles.firstAngle) - strafeStartingAngle) < -acceptableRange){
                v1correction = -increment;
                v2correction = increment;
                v3correction= -increment;
                v4correction = increment;
                encoderSum = 0;
            } else if ((compareAngles(angles.angleUnit, angles.firstAngle) - strafeStartingAngle) > acceptableRange){
                v1correction = increment;
                v2correction = -increment;
                v3correction= increment;
                v4correction = -increment;
                encoderSum = 0;
            }
            v1 = -strafe + v1correction;
            v2 = strafe + v2correction;
            v3 = strafe + v3correction;
            v4 = -strafe + v4correction;
        } else {
            v1 = drive - strafe - turn + v1correction;
            v2 = drive + strafe + turn + v2correction;
            v3 = drive + strafe - turn + v3correction;
            v4 = drive - strafe + turn + v4correction;
        }

        //Change speed factor
        if (gamepad1.left_bumper && speedFactor >= 0.25 && lastPressed!=gamepad1.left_bumper) {
            speedFactor -= .2;
        } else if (gamepad1.right_bumper && speedFactor <= 8.0 && lastPressed!=gamepad1.right_bumper) {
            speedFactor += .2;
        }
        lastPressed = gamepad1.left_bumper || gamepad1.right_bumper;

        // Send calculated power to wheels
        fl.setPower(v1*speedFactor);
        fr.setPower(v2*speedFactor);
        bl.setPower(v3*speedFactor);
        br.setPower(v4*speedFactor);

        // Telemetry output
        telemetry.addData("holdPos", ""+holdPos);
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Strafing: ", " "+strafing);
        telemetry.addData("Turn", " "+(compareAngles(angles.angleUnit, angles.firstAngle) - strafeStartingAngle));
        telemetry.addData("Current Gamepad: ", " "+CurrentGamepad);
        telemetry.addData("Speed Factor", "Speed Factor" + Math.round(speedFactor*10));
        telemetry.addData("Power", "Left Front Power: " + v1);
        telemetry.addData("Power", "Left Right Power: " + v2);
        telemetry.addData("Power", "Back Left Power: " + v3);
        telemetry.addData("Power", "Left Right Power: " + v4);

        telemetry.update();
    }

    @Override
    public void stop() {
    }
}