package com.ruppyrup.bigfun.server;

import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.common.Player;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;

public class Collision {

    public static HitResult hasPlayerHitBall(Player player, Ball ball) {
        HitResult failedHit = new HitResult(false, 0);

        double playerX = player.getCircle().getCenterX();
        double playerY = player.getCircle().getCenterY();
        double ballPositionX = ball.getX();
        double ballPositionY = ball.getY();

        int firstCollisionMargin = 10;

        boolean theBallAndPlayerCloseEnoughToCollide =
                Math.abs(ballPositionX - playerX) <=
                        BALL_RADIUS + PLAYER_RADIUS + firstCollisionMargin &&
                        Math.abs(ballPositionY - playerY) <=
                                BALL_RADIUS + PLAYER_RADIUS + firstCollisionMargin;

        if (!theBallAndPlayerCloseEnoughToCollide) return failedHit;

        int closeCollisionMargin = 2;

        int radiiPrecisionPoints = 100;
        double radiiPrecision = (2 * Math.PI) / radiiPrecisionPoints;

        for (int i = 0; i < radiiPrecisionPoints; i++) {
            double radians = radiiPrecision * i;
            double bx1 = ballPositionX + BALL_RADIUS * Math.cos(radians);
            double px1 = playerX + PLAYER_RADIUS * Math.cos(radians + Math.PI);
            double by1 = ballPositionY + BALL_RADIUS * Math.sin(radians);
            double py1 = playerY + PLAYER_RADIUS * Math.sin(radians + Math.PI);

            boolean firstTest = Math.abs(bx1 - px1) <= closeCollisionMargin && Math.abs(by1 - py1) <= closeCollisionMargin;
            if (firstTest) {
                player.hasJustHitBall();
                System.out.println("Hit angle ball below = " + radians);
                return new HitResult(true, radians);
            }

            double bx2 = ballPositionX + BALL_RADIUS * Math.cos(radians + Math.PI);
            double px2 = playerX + PLAYER_RADIUS * Math.cos(radians);
            double by2 = ballPositionY + BALL_RADIUS * Math.sin(radians + Math.PI);
            double py2 = playerY + PLAYER_RADIUS * Math.sin(radians);
            boolean secondTest = Math.abs(bx2 - px2) <= closeCollisionMargin && Math.abs(by2 - py2) <= closeCollisionMargin;
            if (secondTest) {
                player.hasJustHitBall();
                System.out.println("Hit angle ball above = " + radians);
                return new HitResult(true, radians);
            }
        }
        return failedHit;
    }
}
