package org.firstinspires.ftc.teamcode.reflection;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.annotations.Service;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public abstract class ReflectiveOpMode extends OpMode {

    public final String classPath;

    public Object[] services;

    public ReflectiveOpMode(Class<?> clazz) {
        super();

        this.classPath = clazz.getPackage().toString();

        this.loadServices();
    }

    public Set<Class<?>> findServiceClasses() {
        Reflections reflectionsHelper = new Reflections(this.classPath, new TypeAnnotationsScanner());

        return reflectionsHelper.getTypesAnnotatedWith(Service.class);
    }

    public void loadServices() {

        Set<Class<?>> serviceSet = this.findServiceClasses();
        ArrayList<Object> serviceList = new ArrayList<>();

        for (Class<?> serviceClass : serviceSet) {
            try {
                Constructor<?> constructor = serviceClass.getConstructor();
                Object service = constructor.newInstance();

                serviceList.add(service);
            } catch (NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        this.services = serviceList.toArray();
    }
}
