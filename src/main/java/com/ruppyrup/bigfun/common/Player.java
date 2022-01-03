package com.ruppyrup.bigfun.common;

import javafx.scene.shape.Circle;

import java.util.Date;

public class Player {
    private final Circle circle;
    private final String id;
    private int hitCount;
    private boolean justHitBall;
    private long startTime;
    private long elapsedTime;
    private boolean timerStarted;

    public Player(String id, Circle circle) {
        this.circle = circle;
        this.id = id;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public boolean canHitBallAgain() {
        if (!timerStarted) return true;
        elapsedTime = (new Date()).getTime() - startTime;
        if (elapsedTime < 500) {
            return false;
        } else {
            timerStarted = false;
            return true;
        }
    }

    public boolean hasJustHitBall() {
        startTime = System.currentTimeMillis();
        timerStarted = true;
        return justHitBall;
    }



    public void setJustHitBall(boolean justHitBall) {
        this.justHitBall = justHitBall;
    }

    public Circle getCircle() {
        return circle;
    }

    public String getId() {
        return id;
    }
}
