package org.firstinspires.ftc.teamcode.controllers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Controller {

    protected static HashMap<Class<? extends Controller>, Controller> controllers = new HashMap<>();

    public static <T extends Controller> T getInstance(Class<T> clazz, HardwareMap hardwareMap) {

        // Exists
        if (controllers.get(clazz) != null) {
            return (T) controllers.get(clazz);
        }

        // Make a new one
        try {
            Constructor<T> constructor = clazz.getConstructor(HardwareMap.class);

            Controller instance = constructor.newInstance(hardwareMap);
            controllers.put(clazz, instance);

            return (T) instance;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
