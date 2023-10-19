package com.jokingsun.oilfairy.utils;

/**
 * @author Joshua
 */
public class MathUtil {

    public static double getAngle(double x1, double y1, double x2, double y2) {

        double x = Math.abs(x1 - x2);
        double y = Math.abs(y1 - y2);
        double z = Math.sqrt(x * x + y * y);

        return (Math.asin(y / z) / Math.PI * 180);
    }
}
