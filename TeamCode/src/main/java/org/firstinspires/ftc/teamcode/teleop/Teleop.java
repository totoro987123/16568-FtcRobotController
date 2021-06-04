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
import org.firstinspires.ftc.teamcode.controllers.Outtake;
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

    //Settings
    private double speedFactor = 1;

    //Controllers
    private Ramp ramp;
    private Intake intake;
    private Outtake outtake;
    private IMU imu;

    //Gamepad
    private boolean aPressedLastCycle = false;
    private boolean bPressedLastCycle = false;
    private boolean dPadLastPressed = false;

    @Override
    public void init() {
        this.ramp = Ramp.getInstance(Ramp.class, hardwareMap);
        this.intake = Intake.getInstance(Intake.class, hardwareMap);
        this.outtake = Outtake.getInstance(Outtake.class, hardwareMap);
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

        //DPad
        if (!this.dPadLastPressed) {
            if (this.gamepad1.dpad_up) {
                this.speedFactor = this.speedFactor + .1;
            } else if(this.gamepad2.dpad_down) {
                this.speedFactor = this.speedFactor - .1;
            }
        }
        this.dPadLastPressed = this.gamepad1.dpad_up || this.gamepad1.dpad_down;

        if (this.speedFactor > 1) {
            this.speedFactor = 1;
        } else if (this.speedFactor < 0) {
            this.speedFactor = 0;
        }

        // Send calculated power to wheels
        fl.setPower((drive - strafe - turn) * this.speedFactor);
        fr.setPower((drive + strafe + turn) * this.speedFactor);
        bl.setPower((drive + strafe - turn) * this.speedFactor);
        br.setPower((drive - strafe + turn) * this.speedFactor);

        //Ramp Power
        float percent = gamepad1.left_trigger - gamepad1.right_trigger;
        this.ramp.setPowerPercent(percent);

        //Intake
        if ((!this.aPressedLastCycle) && this.gamepad1.a) {
            this.intake.toggle();
        }
        this.aPressedLastCycle = this.gamepad1.a;

        //Outtake
        if ((!this.bPressedLastCycle) && this.gamepad1.b) {
            this.outtake.toggle();
        }
        this.bPressedLastCycle = this.gamepad1.b;

        // Telemetry output
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addLine("Speed Factor: " + String.valueOf(this.speedFactor));
        telemetry.addLine("Angle: " + String.valueOf(this.imu.getOrientation()));

        telemetry.update();
    }

    @Override
    public void stop() {
        this.outtake.disable();
        this.intake.disable();
        this.ramp.disable();
        this.imu.close();
    }
}