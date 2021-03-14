package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Settings;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class StrafeState extends State {

    private double driveSpeed;
    private double maxSpeed;
    private double distance;
    private int position;
    private int flTargetPosition;
    private int frTargetPosition;
    private int blTargetPosition;
    private int brTargetPosition;
    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;
    private final double wheelCircumference = (1.97 * 2) * Math.PI;
    private int ticksPerTurn = 1120;
    private boolean flReached = false;
    private boolean frReached = false;
    private boolean blReached = false;
    private boolean brReached = false;
    private int threshold = 75;
    private PIDController pidDrive;
    private String direction;
    private Telemetry telemetry;

    private double strafeAngle;
    private Orientation angles;
    private int acceptableRange = 3;
    private double increment = -.25;
    private double power = .65;
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters;

    public StrafeState(double target, double speed, HardwareMap hardwareMap) {
        super(); //set the hardwareMap
        distance = target;
        driveSpeed = speed;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        //reverse directions for tile-runner
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
    }

    //new method for beta PID-drive
    public StrafeState(double distance, HardwareMap hardwareMap, String direction, Telemetry telemetry) {
        super(); //set the hardwareMap
        this.distance = distance;
        this.maxSpeed = maxSpeed;
        this.direction = direction;
        this.telemetry = telemetry;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        //reverse directions for tile-runner
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
        this.running = true;

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        strafe(distance);

        //this part needs to be changed, because it sets the power twice
        //consider just having a method for this, might be simpler
        //also we need to account for the initial drive speed set by the user
        //in the PID controller, set a threshold value for power so that it never goes under that amt (like 0.1)
        //pidDrive = new PIDController(1.7, 0.001, 0.6, hardwareMap, flTargetPosition, maxSpeed);
        //driveSpeed = pidDrive.PIDControl();

        //drive(driveSpeed);
    }

    @Override
    public void update() {
        flReached = Math.abs(fl.getCurrentPosition()) >= Math.abs(flTargetPosition) - threshold;
        //frReached = Math.abs(fr.getCurrentPosition()) >= Math.abs(frTargetPosition) - threshold;
        frReached = true;
        blReached = Math.abs(bl.getCurrentPosition()) >= Math.abs(blTargetPosition) - threshold;
        brReached = Math.abs(br.getCurrentPosition()) >= Math.abs(brTargetPosition) - threshold;

        if (flReached && frReached && blReached && brReached){
            this.stop();
            this.goToNextState();
        }
        else {
            double v1correction = 0;
            double v2correction = 0;
            double v3correction = 0;
            double v4correction = 0;
            if (compareAngles(angles.angleUnit, angles.firstAngle) - strafeAngle < -acceptableRange){
                v1correction = -increment;
                v2correction = increment;
                v3correction= -increment;
                v4correction = increment;
            } else if (compareAngles(angles.angleUnit, angles.firstAngle) - strafeAngle > acceptableRange) {
                v1correction = increment;
                v2correction = -increment;
                v3correction = increment;
                v4correction = -increment;
            }
            fl.setPower(power+v1correction);
            fr.setPower(power+v2correction);
            bl.setPower(power+v3correction);
            br.setPower(power+v4correction);
        }

        telemetry.addLine("FL Diff: " + Math.abs(fl.getCurrentPosition() - flTargetPosition));
        //telemetry.addLine("FR Power: " + fl);
        telemetry.addLine("BL Diff: " + Math.abs(bl.getCurrentPosition() - blTargetPosition));
        telemetry.addLine("BR Diff: " + Math.abs(br.getCurrentPosition() - brTargetPosition));
        telemetry.update();
    }

    @Override
    public void stop() {
        drive(0);
        this.running = false;
    }

    @Override
    public String toString() {
        return "DriveState: Power = " + driveSpeed + ", Distance =" + distance;
    }

    public double compareAngles(AngleUnit angleUnit, double angle) {
        double degrees = AngleUnit.DEGREES.fromUnit(angleUnit, angle);

        return AngleUnit.DEGREES.normalize(degrees);
    }

    private void strafe(double distance) {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        strafeAngle = compareAngles(angles.angleUnit, angles.firstAngle);

        position = TickService.inchesToTicks(distance);

        flTargetPosition = fl.getCurrentPosition() - position;
        frTargetPosition = fr.getCurrentPosition() + position;
        blTargetPosition = bl.getCurrentPosition() + position;
        brTargetPosition = br.getCurrentPosition() - position;

        fl.setTargetPosition(flTargetPosition);
        fr.setTargetPosition(frTargetPosition);
        bl.setTargetPosition(blTargetPosition);
        br.setTargetPosition(brTargetPosition);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        fl.setPower(-power);
        fr.setPower(power);
        bl.setPower(power);
        br.setPower(-power);
    }

    //obsolete once all four enocders are used, can simply set a constant power
    //the target encoder value is what matters
    public void drive(double power) {
        switch (direction) {
            case "front":
                fl.setPower(power);
                fr.setPower(power);
                bl.setPower(power);
                br.setPower(power);
                break;
            case "back":
                fl.setPower(-power);
                fr.setPower(-power);
                bl.setPower(-power);
                br.setPower(-power);
                break;
            case "left":
                fl.setPower(-power);
                fr.setPower(power);
                bl.setPower(power);
                br.setPower(-power);
                break;
            case "right":
                fl.setPower(power);
                fr.setPower(-power);
                bl.setPower(-power);
                br.setPower(power);
                break;
        }
    }

    //target position changes based on direction of motion
    private void setTargets() {
        switch (direction) {
            case "front":
                flTargetPosition = position;
                //frTargetPosition = position;
                blTargetPosition = position;
                brTargetPosition = position;
                break;
            case "back":
                flTargetPosition = -position;
                //frTargetPosition = -position;
                blTargetPosition = -position;
                brTargetPosition = -position;
                break;
            case "left":
                flTargetPosition = -position;
                //frTargetPosition = position;
                blTargetPosition = position;
                brTargetPosition = -position;
                break;
            case "right":
                flTargetPosition = position;
                //frTargetPosition = -position;
                blTargetPosition = -position;
                brTargetPosition = position;
                break;
        }
        fl.setTargetPosition(flTargetPosition);
        //fr.setTargetPosition(-position);
        bl.setTargetPosition(blTargetPosition);
        br.setTargetPosition(brTargetPosition);
    }

    public double getPower() {
        return fr.getPower();
    }

    private int distToTicks(double distance) {
        double circumferenceTraveled = distance / wheelCircumference;
        return (int) (ticksPerTurn * circumferenceTraveled); //encoder value
    }
}
