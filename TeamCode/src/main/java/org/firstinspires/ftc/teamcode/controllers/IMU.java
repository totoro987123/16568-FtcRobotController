package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class IMU extends Controller {

    // Instance Variables
    private boolean inited = false;
    private final AngleUnit unit = AngleUnit.DEGREES;
    private final HardwareMap hardwareMap;
    private BNO055IMU imuInstance;
    private BNO055IMU.Parameters parameters;

    private float defaultAngle = 0;

    // Constructor
    public IMU(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        this.parameters = new BNO055IMU.Parameters();
        this.parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        this.parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        this.parameters.loggingEnabled = true;
        this.parameters.loggingTag = "IMU";

        this.imuInstance = hardwareMap.get(BNO055IMU.class, "imu");
    }

    // Methods

    public void initialize() {
        if (!this.inited) {
            this.imuInstance.initialize(this.parameters);
            this.inited = true;
        }
    }

    public void setDefaultOrientation() {
        this.defaultAngle = this.getRawOrientation();
    }

    public float getOrientation() {
        return this.getRawOrientation() - this.defaultAngle;
    }

    public float getRawOrientation() {
        return this.imuInstance.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, unit).firstAngle;
    }

    public void close() {
        this.imuInstance.close();
        controller = null;
    }

}
