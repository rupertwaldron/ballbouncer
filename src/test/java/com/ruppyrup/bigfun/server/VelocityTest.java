package com.ruppyrup.bigfun.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VelocityTest {

    private Velocity velocity;

    @BeforeEach
    void setup() {
        velocity = new Velocity(4, 3);
    }

    @Test
    void setVelocityFromXAndY() {
        double magnitude = velocity.getMagnitude();
        double radianAngle = velocity.getRadianAngle();

        Assertions.assertEquals(5, magnitude);
        Assertions.assertEquals(0.64, radianAngle);
    }

    @Test
    void setVelocityFromMagAndAngle() {

        velocity.setVelocityFromMagAndAngle(5, 0.6435);

        double x = velocity.getxMagnitude();
        double y = velocity.getyMagnitude();

        Assertions.assertEquals(4, x);
        Assertions.assertEquals(2.99, y);
    }
}
