package com.ruppyrup.bigfun.server;

public class HitResult {
    public final boolean isHit;
    public final double radians;

    public HitResult(boolean isHit, double radians) {
        this.isHit = isHit;
        this.radians = radians;
    }
}
