package com.ruppyrup.bigfun.common;

import javafx.scene.shape.Circle;

import java.util.Date;

public class Ball {
    private final Circle circle;

    public Ball(Circle circle) {
        this.circle = circle;
    }

    public double getX() {
        return circle.getCenterX();
    }

    public double getY() {
        return circle.getCenterY();
    }

    public void setX(double x) {
        circle.setCenterX(x);
    }

    public void setY(double y) {
        circle.setCenterY(y);
    }

    public double getRadius() {
        return circle.getRadius();
    }

}
