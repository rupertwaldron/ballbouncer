package com.ruppyrup.bigfun.server;

import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.common.Player;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;

public class Collision {

  private static ExecutorService executors = startExecutors();

  private static ExecutorService startExecutors() {
    return Executors.newFixedThreadPool(2,
        (Runnable r) -> {
          Thread t = new Thread(r);
          t.setDaemon(true);
          return t;
        });
  }

  public static HitResult hasPlayerHitBall(Player player, Ball ball) {
    HitResult failedHit = new HitResult(false, 0);

    double playerX = player.getCircle().getCenterX();
    double playerY = player.getCircle().getCenterY();
    double ballPositionX = ball.getX();
    double ballPositionY = ball.getY();

    int firstCollisionMargin = 5;

    boolean theBallAndPlayerCloseEnoughToCollide =
        Math.abs(ballPositionX - playerX) <=
            BALL_RADIUS + PLAYER_RADIUS + firstCollisionMargin &&
            Math.abs(ballPositionY - playerY) <=
                BALL_RADIUS + PLAYER_RADIUS + firstCollisionMargin;

    if (!theBallAndPlayerCloseEnoughToCollide) {
      return failedHit;
    }

    int closeCollisionMargin = 2;

    int radiiPrecisionPoints = 200;
    double radiiPrecision = (2 * Math.PI) / radiiPrecisionPoints;

    CompletableFuture<HitResult> hitResultBelowFuture = CompletableFuture.supplyAsync(
        () -> getHitResultBelow(playerX, playerY, ballPositionX,
            ballPositionY, closeCollisionMargin, radiiPrecisionPoints, radiiPrecision), executors);

    CompletableFuture<HitResult> hitResultAboveFuture = CompletableFuture.supplyAsync(
        () -> getHitResultAbove(playerX, playerY, ballPositionX,
            ballPositionY, closeCollisionMargin, radiiPrecisionPoints, radiiPrecision), executors);

    return CompletableFuture.anyOf(hitResultBelowFuture, hitResultAboveFuture)
        .thenApply(hitResult -> (HitResult) Objects.requireNonNullElse(hitResult, failedHit)).join();
  }

  private static HitResult getHitResultAbove(double playerX, double playerY, double ballPositionX,
      double ballPositionY, int closeCollisionMargin, int radiiPrecisionPoints,
      double radiiPrecision) {
    for (int i = 0; i < radiiPrecisionPoints; i++) {
      double radians = radiiPrecision * i;
      double bx2 = ballPositionX + BALL_RADIUS * Math.cos(radians + Math.PI);
      double px2 = playerX + PLAYER_RADIUS * Math.cos(radians);
      double by2 = ballPositionY + BALL_RADIUS * Math.sin(radians + Math.PI);
      double py2 = playerY + PLAYER_RADIUS * Math.sin(radians);
      boolean secondTest = Math.abs(bx2 - px2) <= closeCollisionMargin
          && Math.abs(by2 - py2) <= closeCollisionMargin;
      if (secondTest) {
//        System.out.println("Hit angle ball above = " + radians);
        return new HitResult(true, radians);
      }
    }
    return null;
  }

  private static HitResult getHitResultBelow(double playerX, double playerY, double ballPositionX,
      double ballPositionY, int closeCollisionMargin, int radiiPrecisionPoints,
      double radiiPrecision) {
    for (int i = 0; i < radiiPrecisionPoints; i++) {
      double radians = radiiPrecision * i;
      double bx1 = ballPositionX + BALL_RADIUS * Math.cos(radians);
      double px1 = playerX + PLAYER_RADIUS * Math.cos(radians + Math.PI);
      double by1 = ballPositionY + BALL_RADIUS * Math.sin(radians);
      double py1 = playerY + PLAYER_RADIUS * Math.sin(radians + Math.PI);

      boolean firstTest = Math.abs(bx1 - px1) <= closeCollisionMargin
          && Math.abs(by1 - py1) <= closeCollisionMargin;
      if (firstTest) {
//        System.out.println("Hit angle ball below = " + radians);
        return new HitResult(true, radians + Math.PI);
      }
    }
    return null;
  }
}
