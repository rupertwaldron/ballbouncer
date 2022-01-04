package com.ruppyrup.bigfun.common;

import com.ruppyrup.bigfun.server.Velocity;
import javafx.scene.shape.Circle;

public class Ball {
    private final Circle circle;
    private final Velocity velocity;

    public Ball(Circle circle, Velocity velocity) {
        this.circle = circle;
        this.velocity = velocity;
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

    public Velocity getVelocity() {
        return velocity;
    }
}
