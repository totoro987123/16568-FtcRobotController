package org.firstinspires.ftc.teamcode.framework;

public final class WrapperHelper {

    public static Class<?> getTrueClass(Object obj){
        if (obj instanceof Byte) {
            return Byte.TYPE;
        } else if (obj instanceof Short) {
            return Short.TYPE;
        } else if (obj instanceof Integer) {
            return Integer.TYPE;
        } else if (obj instanceof Float) {
            return Float.TYPE;
        } else if (obj instanceof Long) {
            return Long.TYPE;
        } else if (obj instanceof Double) {
            return Double.TYPE;
        } else if (obj instanceof Boolean) {
            return Boolean.TYPE;
        } else if (obj instanceof Character) {
            return Character.TYPE;
        }

        return obj.getClass();
    }

}
