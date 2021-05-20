package org.firstinspires.ftc.teamcode.framework;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.framework.annotations.Component;
import org.firstinspires.ftc.teamcode.framework.annotations.Initialize;
import org.firstinspires.ftc.teamcode.framework.annotations.Injected;
import org.reflections8.Reflections;
import org.reflections8.scanners.TypeAnnotationsScanner;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class StandardOpMode extends OpMode {

    private final String opModeClassPath;

    // MAKE PRIVATE
    protected Map<Class<?>, Object> loadables = new HashMap<Class<?>,Object>();

    public StandardOpMode(Class<?> clazz) {
        super();

        System.out.println("Running standard op mode!");

        this.opModeClassPath = clazz.getPackage().getName();
    }

    public void initFramework() {
        this.initializeOpModeVariables();
        this.loadServices();

        this.loadAllFields();

        this.loadFields(this);
    }

    private void initializeOpModeVariables() {
        this.loadables.put(OpMode.class, this);
        this.loadables.put(Telemetry.class, this.telemetry);
        this.loadables.put(HardwareMap.class, this.hardwareMap);
    }

    private Set<Class<?>> findServiceClasses() {
        Reflections reflectionsHelper = new Reflections(this.opModeClassPath, new TypeAnnotationsScanner());

        return reflectionsHelper.getTypesAnnotatedWith(Component.class);
    }

    private void loadServices() {

        Set<Class<?>> serviceSet = this.findServiceClasses();

        for (Class<?> serviceClass : serviceSet) {
            try {
                Constructor<?> constructor = serviceClass.getConstructor();
                Object service = constructor.newInstance();

                this.telemetry.addLine("Library: " + serviceClass.getName());

                this.loadables.put(serviceClass, service);
            } catch (NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        this.telemetry.update();
    }

    private void initialize(Object object) {
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Initialize.class)) {
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadFields(Object object) {
        Field[] fields = object.getClass().getFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Injected.class)) {
                try {
                    field.set(object, this.loadables.get(field.getType()));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadAllFields() {
        for (Object object : this.loadables.values()) {
            this.loadFields(object);
        }

        for (Object object : this.loadables.values()) {
            this.initialize(object);
        }
    }

    public <T> T create(Class<T> clazz, Object ... params) {
        T obj;

        if (params.length > 0) {
            // GENERATE THE ARG PATTERN
            Class<?>[] argPattern = new Class[params.length];

            for (int i = 0; i < params.length; i++) {
                Class<?> classType = WrapperHelper.getTrueClass(params[i]);

                argPattern[i] = classType;
            }

            // GET CONSTRUCTOR
            try {
                Constructor<T> constructor = clazz.getConstructor(argPattern);

                obj = constructor.newInstance(params);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                Constructor<T> constructor = clazz.getConstructor();

                obj = constructor.newInstance();
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }

        }

        // SET VALUES
        this.loadFields(obj);

        this.initialize(obj);

        return obj;
    }
}
