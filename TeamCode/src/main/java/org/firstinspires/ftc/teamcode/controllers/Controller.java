package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Controller {

    protected static Controller controller;

    public static <T extends Controller> T getInstance(Class<T> clazz, HardwareMap hardwareMap) {

        // Exists
        if (controller != null) {
            return (T) controller;
        }

        // Make a new one
        try {
            Constructor<T> constructor = clazz.getConstructor(HardwareMap.class);

            controller = constructor.newInstance(hardwareMap);

            return (T) controller;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
