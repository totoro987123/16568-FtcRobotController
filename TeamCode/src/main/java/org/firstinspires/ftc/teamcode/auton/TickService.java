package org.firstinspires.ftc.teamcode.auton;

public final class TickService {

    private static final double wheelCircumference = (1.97 * 2) * Math.PI;
    private static final int ticksPerTurn = 1120;

    public static int inchesToTicks(double inches) {
        double circumferenceTraveled = inches / wheelCircumference;
        int ticksTraveled = (int) (ticksPerTurn * circumferenceTraveled);

        return ticksTraveled;
    }
}
