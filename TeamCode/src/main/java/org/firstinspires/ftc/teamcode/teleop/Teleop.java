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
import org.firstinspires.ftc.teamcode.controllers.IMU;
import org.firstinspires.ftc.teamcode.controllers.Intake;
import org.firstinspires.ftc.teamcode.controllers.Ramp;

@TeleOp(name="Teleop", group="Iterative Opmode")

public class Teleop extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Drive
    private DcMotor fl = null;
    private DcMotor fr = null;
    private DcMotor bl = null;
    private DcMotor br = null;

    //Controllers
    private Ramp ramp;
    private Intake intake;
    private IMU imu;

    //Gamepas
    private boolean aPressedLastCycle = false;


    @Override
    public void init() {
        this.intake = Intake.getInstance(Intake.class, hardwareMap);
        this.ramp = Ramp.getInstance(Ramp.class, hardwareMap);
        this.imu = IMU.getInstance(IMU.class, hardwareMap);

        // Wheel init
        fl = hardwareMap.dcMotor.get(Settings.FRONT_LEFT);
        fr = hardwareMap.dcMotor.get(Settings.FRONT_RIGHT);
        bl = hardwareMap.dcMotor.get(Settings.BACK_LEFT);
        br = hardwareMap.dcMotor.get(Settings.BACK_RIGHT);

        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);

        // Telemetry
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() { }

    @Override
    public void start() {
        runtime.reset();
        this.imu.setDefaultOrientation();
    }

    @Override
    public void loop() {
        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        // Send calculated power to wheels
        fl.setPower(drive - strafe - turn);
        fr.setPower(drive + strafe + turn);
        bl.setPower(drive + strafe - turn);
        br.setPower(drive - strafe + turn);

        //Ramp Power
        float percent = gamepad1.left_trigger - gamepad1.right_trigger;
        this.ramp.setPowerPercent(percent);

        //Intake
        if ((!this.aPressedLastCycle) && this.gamepad1.a) {
            this.intake.toggle();
        }
        this.aPressedLastCycle = this.gamepad1.a;

        // Telemetry output
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addLine("Angle: " + String.valueOf(this.imu.getOrientation()));

        telemetry.update();
    }

    @Override
    public void stop() {
    }
}