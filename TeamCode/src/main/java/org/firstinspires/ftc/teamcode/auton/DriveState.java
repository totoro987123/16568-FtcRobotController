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
    private final double wheelCircumference = (1.97 * 2) * Math.PI;
    private int ticksPerTurn = 1120;
    private boolean flReached = false;
    private boolean frReached = false;
    private boolean blReached = false;
    private boolean brReached = false;
    private int threshold = 20;
    private PIDController pidDrive;
    private String direction;

    public DriveState(double target, double speed, HardwareMap hardwareMap) {
        super(hardwareMap); //set the hardwareMap
        distance = target;
        driveSpeed = speed;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        //instead of making direction negative to reverse it, try flipping this
        fl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);
    }

    //for beta PID-drive
    public DriveState(double target, HardwareMap hardwareMap, String direction) {
        super(hardwareMap); //set the hardwareMap
        distance = target;
        this.direction = direction;

        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        //instead of making direction negative to reverse it, try flipping this
        fl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {
        this.running = true;

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /**
        int currentPosition = (int)((fl.getCurrentPosition() +
                                    fr.getCurrentPosition() +
                                    bl.getCurrentPosition() +
                                    br.getCurrentPosition()) / 4.0);
        */
        int currentPosition = (int)((fl.getCurrentPosition() +
                bl.getCurrentPosition() +
                br.getCurrentPosition()) / 3.0);

        position = currentPosition + distToTicks(distance);

        fl.setTargetPosition(position);
        //fr.setTargetPosition(position);
        bl.setTargetPosition(position);
        br.setTargetPosition(position);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //this part needs to be changed, because it sets the power twice
        //consider just having a method for this, might be simpler
        //also we need to account for the initial drive speed set by the user
        //in the PID controller, set a threshold value for power so that it never goes under that amt (like 0.1)
        pidDrive = new PIDController(1.5, 0.01, 0.6, hardwareMap, position);
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

        flReached = Math.abs(fl.getCurrentPosition() - position) < threshold;
        frReached = true;
        blReached = Math.abs(bl.getCurrentPosition() - position) < threshold;
        brReached = Math.abs(br.getCurrentPosition() - position) < threshold;

        if (flReached && frReached && blReached && brReached) {
            this.stop();
            this.goToNextState();
        }
        else {
            pidDrive.PIDControl();
        }
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

    public void drive(double power) {
        fl.setPower(power);
        fr.setPower(power);
        bl.setPower(power);
        br.setPower(power);
    }

    private int distToTicks(double distance) {
        double circumferenceTraveled = distance / wheelCircumference;
        return (int) (ticksPerTurn * circumferenceTraveled); //encoder value
    }
}
