package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Settings;

public class DriveState extends State {

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
    private double realSpeed;

    public DriveState(double target, double speed, HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap);
        this.telemetry = telemetry;
        distance = target;
        driveSpeed = speed;
    }

    //new method for beta PID-drive
    public DriveState(double distance, double maxSpeed, String direction, HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap);
        this.distance = distance;
        this.maxSpeed = maxSpeed;
        this.direction = direction;
        this.telemetry = telemetry;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        //reverse directions for tile-runner

         fl.setDirection(DcMotor.Direction.FORWARD);
         fr.setDirection(DcMotor.Direction.REVERSE);
         bl.setDirection(DcMotor.Direction.FORWARD);
         br.setDirection(DcMotor.Direction.REVERSE);

        /**
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);
         */
    }

    @Override
    public void start() {
        this.running = true;

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /**
        int currentPosition = (int)((fl.getCurrentPosition() +
                                    fr.getCurrentPosition() +
                                    bl.getCurrentPosition() +
                                    br.getCurrentPosition()) / 4.0);
        */
        int currentPosition = ((fl.getCurrentPosition() + fr.getCurrentPosition()+
                bl.getCurrentPosition() +
                br.getCurrentPosition()) / 4);

        //position = currentPosition + distToTicks(distance);
        position = currentPosition + TickService.inchesToTicks(distance);
        //int flTargetPosition = getFlTargetPosition(); //need fl motor position for PID calculations

        setTargets(); //set target positions for each motor

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //this part needs to be changed, because it sets the power twice
        //consider just having a method for this, might be simpler
        //also we need to account for the initial drive speed set by the user
        //in the PID controller, set a threshold value for power so that it never goes under that amt (like 0.1)
        pidDrive = new PIDController(1.7, 0.001, 0.6, hardwareMap, flTargetPosition, maxSpeed);
        driveSpeed = pidDrive.PIDControl();

        drive(driveSpeed);
    }

    @Override
    public void update() {
        /**
        flReached = Math.abs(fl.getCurrentPosition()) >= Math.abs(position) - threshold;
        //frReached = Math.abs(fr.getCurrentPosition()) >= Math.abs(position) - threshold;
        frReached = true;
        blReached = Math.abs(bl.getCurrentPosition()) >= Math.abs(position) - threshold;
        brReached = Math.abs(br.getCurrentPosition()) >= Math.abs(position) - threshold;
         */

        flReached = Math.abs(fl.getCurrentPosition() - flTargetPosition) < threshold;
        frReached = Math.abs(fr.getCurrentPosition() - frTargetPosition) < threshold;
        blReached = Math.abs(bl.getCurrentPosition() - blTargetPosition) < threshold;
        brReached = Math.abs(br.getCurrentPosition() - brTargetPosition) < threshold;

        realSpeed = pidDrive.getActualSpeed();

        if (flReached && frReached && blReached && brReached) {
            this.stop();
            this.goToNextState();
        }
        else {
            driveSpeed = pidDrive.PIDControl();
            drive(driveSpeed);
        }

        telemetry.addLine("FL Diff: " + Math.abs(fl.getCurrentPosition() - flTargetPosition));
        telemetry.addLine("FR Power: " + fl);
        telemetry.addLine("BL Diff: " + Math.abs(bl.getCurrentPosition() - blTargetPosition));
        telemetry.addLine("BR Diff: " + Math.abs(br.getCurrentPosition() - brTargetPosition));
        telemetry.addLine("actualSpeed : " + realSpeed);
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
                frTargetPosition = position;
                blTargetPosition = position;
                brTargetPosition = position;
                break;
            case "back":
                flTargetPosition = -position;
                frTargetPosition = -position;
                blTargetPosition = -position;
                brTargetPosition = -position;
                break;
            case "left":
                flTargetPosition = -position;
                frTargetPosition = position;
                blTargetPosition = position;
                brTargetPosition = -position;
                break;
            case "right":
                flTargetPosition = position;
                frTargetPosition = -position;
                blTargetPosition = -position;
                brTargetPosition = position;
                break;
        }
        fl.setTargetPosition(flTargetPosition);
        fr.setTargetPosition(frTargetPosition);
        bl.setTargetPosition(blTargetPosition);
        br.setTargetPosition(brTargetPosition);
    }
}
