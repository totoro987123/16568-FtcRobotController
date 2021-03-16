package org.firstinspires.ftc.teamcode.framework;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.framework.annotations.*;
import org.firstinspires.ftc.teamcode.framework.reflection.AnnotationDetector;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SystemOpMode extends OpMode {

    private final String classPath;

    private final Map<Class<?>, Object> loadables = new HashMap<Class<?>,Object>();

    public SystemOpMode(Class<?> clazz) {
        super();

        this.classPath = clazz.getPackage().getName();

        this.loadOpModeVariables();
        this.loadServices();

        this.loadAllFields();

        this.loadFields(this);
    }

    private void loadOpModeVariables() {
        this.loadables.put(OpMode.class, this);
        this.loadables.put(Telemetry.class, this.telemetry);
        this.loadables.put(HardwareMap.class, this.hardwareMap);
    }

    private void loadFields(Object object) {
        Field[] fields = object.getClass().getFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Loadable.class)) {
                try {
                    field.set(object, this.loadables.get(field.getType()));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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


    private void loadAllFields() {
        for (Object object : this.loadables.values()) {
            this.loadFields(object);
        }

        for (Object object : this.loadables.values()) {
            this.initialize(object);
        }
    }


    private Set<Class<?>> findServiceClasses() {
        final AnnotationDetector.TypeReporter reporter = new AnnotationDetector.TypeReporter() {

            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                try {
                    Class<?> serviceClass = Class.forName(className);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{Service.class};
            }
        };

        final AnnotationDetector cf = new AnnotationDetector(reporter);

        try {
            cf.detect();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Reflections reflectionsHelper = new Reflections(this.classPath, new SubTypesScanner(), new TypeAnnotationsScanner());

        return reflectionsHelper.getTypesAnnotatedWith(Service.class);
    }

    private void loadService(Class<?> serviceClass) {
        try {
            Constructor<?> constructor = serviceClass.getConstructor();
            Object service = constructor.newInstance();

            this.loadables.put(serviceClass, service);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void loadServices() {

        Set<Class<?>> serviceSet = this.findServiceClasses();

        for (Class<?> serviceClass : serviceSet) {
            try {
                Constructor<?> constructor = serviceClass.getConstructor();
                Object service = constructor.newInstance();

                this.loadables.put(serviceClass, service);
            } catch (NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
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
