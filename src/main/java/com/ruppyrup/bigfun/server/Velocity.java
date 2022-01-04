package com.ruppyrup.bigfun.server;

public class Velocity {
    private double magnitude;
    private double radianAngle;
    private double xMagnitude;
    private double yMagnitude;

    public Velocity(double xMagnitude, double yMagnitude) {
        this.xMagnitude = xMagnitude;
        this.yMagnitude = yMagnitude;
        setVelocityFromXAndY(xMagnitude, yMagnitude);
    }

    public void setVelocityFromXAndY(double x, double y) {
        xMagnitude = x;
        yMagnitude = y;
        magnitude = Math.floor(100 * Math.sqrt(x * x + y * y)) / 100;
        radianAngle = Math.floor(100 * Math.atan(y / x)) / 100;
    }

    public void setVelocityFromMagAndAngle(double mag, double angle) {
        magnitude = mag;
        radianAngle = angle;
        xMagnitude = Math.floor(100 * mag * Math.cos(angle)) / 100;
        yMagnitude = Math.floor(100 * mag * Math.sin(angle)) / 100;

    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getRadianAngle() {
        return radianAngle;
    }

    public double getxMagnitude() {
        return xMagnitude;
    }

    public double getyMagnitude() {
        return yMagnitude;
    }

    @Override
    public String toString() {
        return "Velocity{" +
                "magnitude=" + magnitude +
                ", radianAngle=" + radianAngle +
                ", xMagnitude=" + xMagnitude +
                ", yMagnitude=" + yMagnitude +
                '}';
    }
}
