package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Settings;

public class DriveState extends State {

    private double driveSpeed;
    private double distance;
    private int position;
    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters;
    private final double wheelCircumference = (1.97 * 2) * Math.PI;
    private int ticksPerTurn = 1120;
    private boolean flReached = false;
    private boolean frReached = false;
    private boolean blReached = false;
    private boolean brReached = false;
    private int threshold = 20;

    public DriveState(double target, double speed, HardwareMap hardwareMap) {
        super(hardwareMap); //set the hardwareMap
        distance = target;
        driveSpeed = speed;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        fl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);

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
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int currentPosition = (int)((fl.getCurrentPosition() +
                                    fr.getCurrentPosition() +
                                    bl.getCurrentPosition() +
                                    br.getCurrentPosition()) / 4.0);

        position = currentPosition + distToTicks(distance);

        fl.setTargetPosition(position);
        fr.setTargetPosition(position);
        bl.setTargetPosition(position);
        br.setTargetPosition(position);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        fl.setPower(driveSpeed);
        fr.setPower(driveSpeed);
        bl.setPower(driveSpeed);
        br.setPower(driveSpeed);
    }

    @Override
    public void update() {
        flReached = Math.abs(fl.getCurrentPosition()) >= Math.abs(position) - threshold;
        frReached = Math.abs(fr.getCurrentPosition()) >= Math.abs(position) - threshold;
        blReached = Math.abs(bl.getCurrentPosition()) >= Math.abs(position) - threshold;
        brReached = Math.abs(br.getCurrentPosition()) >= Math.abs(position) - threshold;

        if (flReached && frReached && blReached && brReached) {
            this.stop();
            this.goToNextState();
        }
    }

    @Override
    public void stop() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        this.running = false;
    }

    @Override
    public String toString() {
        return "DriveState: Power = " + driveSpeed + ", Distance =" + distance;
    }

    private int distToTicks(double distance) {
        double circumferenceTraveled = distance / wheelCircumference;
        return (int) (ticksPerTurn * circumferenceTraveled); //encoder value
    }
}
