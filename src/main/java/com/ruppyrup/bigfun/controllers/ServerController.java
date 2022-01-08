package com.ruppyrup.bigfun.controllers;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.common.Player;
import com.ruppyrup.bigfun.server.Collision;
import com.ruppyrup.bigfun.server.EchoMultiServer;
import com.ruppyrup.bigfun.server.HitResult;
import com.ruppyrup.bigfun.server.Velocity;
import com.ruppyrup.bigfun.utils.Position;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ServerController implements Initializable {

  private EchoMultiServer echoMultiServer;
  private final Map<String, Player> players = new HashMap<>();
  private static final int cores = Runtime.getRuntime().availableProcessors();
  private ExecutorService executor;

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private JFXButton resetButton;

  @FXML
  void reset(ActionEvent event) {
    ball.setX(100);
    ball.setY(100);
    ball.getVelocity().setVelocityFromXAndY(3, 3);

    players.values().forEach(player -> {
      player.setHitCount(0);
      echoMultiServer.sendHitCount(player.getId(), 0);
    });
  }

  private Ball ball;

  public Ball getBall() {
    return ball;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    echoMultiServer = new EchoMultiServer(this);
    echoMultiServer.start();

    Circle ballCircle = createCircle(BALL_RADIUS, Color.ORANGE, new Position(100, 100));
    Velocity ballVelocity = new Velocity(3, 3);
    ball = new Ball(ballCircle, ballVelocity);

    bounceBall();

    echoMultiServer.setOnSucceeded(
        event -> System.out.println("Succeeded :: " + echoMultiServer.getValue()));
  }

  public void addNewPlayer(String id) {
    System.out.println("Adding new button");
    Text name = new Text(id.substring(15));
    String color = getRandomRGBColor();
    System.out.println("Color :: " + color);
    Circle newPlayer = createCircle(PLAYER_RADIUS,
        Color.valueOf(color),
        new Position(
            getRandom().nextDouble() * 400.0,
            getRandom().nextDouble() * 400.0));

    players.put(id, new Player(id, newPlayer));
  }

  public void removePlayer(String id) {
    Circle playerToRemove = players.get(id).getCircle();
    playerToRemove.setDisable(true);
    playerToRemove.setVisible(false);
    players.remove(id);
  }


  private void bounceBall() {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), t -> {
      //move the ball
      double ballPositionX = ball.getX() + ball.getVelocity().getxMagnitude();
      double ballPositionY = ball.getY() + ball.getVelocity().getyMagnitude();

      Velocity ballVelocity = ball.getVelocity();

      double ballSpeed = ballVelocity.getMagnitude();

      HitResult hit = getHitResult();

      if (hit.isHit) {
        System.out.println("Client hit by ball:: ");
        ball.getVelocity().setVelocityFromMagAndAngle(ballSpeed, hit.radians);
      }

      Bounds bounds = anchorPane.getLayoutBounds();
      final boolean atRightBorder = ballPositionX >= (bounds.getMaxX() - ball.getRadius());
      final boolean atLeftBorder = ballPositionX <= (bounds.getMinX() + ball.getRadius());
      final boolean atBottomBorder = ballPositionY >= (bounds.getMaxY() - ball.getRadius());
      final boolean atTopBorder = ballPositionY <= (bounds.getMinY() + ball.getRadius());

      if (atRightBorder || atLeftBorder) {
//                ballDirection = ballVelocity.getRadianAngle();
//                System.out.println("Ball velocity 1 :: " + ballVelocity);
        ballVelocity.setVelocityFromXAndY(-ballVelocity.getxMagnitude(),
            ballVelocity.getyMagnitude());
//                System.out.println("Ball velocity 2 :: " + ballVelocity);
      }

      if (atBottomBorder || atTopBorder) {
//                ballDirection = ballVelocity.getRadianAngle();
//                System.out.println("Ball velocity 5 :: " + ballVelocity);
        ballVelocity.setVelocityFromXAndY(ballVelocity.getxMagnitude(),
            -ballVelocity.getyMagnitude());
//                System.out.println("Ball velocity 6 :: " + ballVelocity);
      }

      echoMultiServer.sendBallPosition(ballPositionX, ballPositionY);

      ball.setX(ballPositionX);
      ball.setY(ballPositionY);
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  private HitResult getHitResult() {
    return players.values().stream()
        .filter(Player::canHitBallAgain)
        .map(player -> {
          HitResult hitResult = Collision.hasPlayerHitBall(player, ball);
          if (hitResult.isHit) {
            player.hasJustHitBall();
            echoMultiServer.sendHitCount(player.getId(), player.getHitCount());
          }
          return hitResult;
        })
        .filter(hitResult -> hitResult.isHit)
        .findFirst()
        .orElse(new HitResult(false, 0));
  }

  public void moveOtherPlayer(String id, double xValue, double yValue) {
    Circle playerToMove = Optional.ofNullable(players.get(id)).map(Player::getCircle).orElse(null);
    if (playerToMove == null) {
      return;
    }
    transitionNode(playerToMove, xValue, yValue, 150);
  }

  private void transitionNode(Circle nodeToMove, double xValue, double yValue, int duration) {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), t -> {
      nodeToMove.setCenterX(xValue);
      nodeToMove.setCenterY(yValue);
    }));
    timeline.setCycleCount(1);
    timeline.play();
  }

  private Circle createCircle(int radius, Paint color, Position startPosition) {
    Circle circle = new Circle(radius, color);
    circle.setCenterX(startPosition.getX());
    circle.setCenterY(startPosition.getY());
    Text text = new Text("42");
//        StackPane stack = new StackPane();
//        stack.getChildren().addAll(circle, text);
    anchorPane.getChildren().add(circle);
    return circle;
  }
}
