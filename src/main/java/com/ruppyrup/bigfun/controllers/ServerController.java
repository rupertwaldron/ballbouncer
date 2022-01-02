package com.ruppyrup.bigfun.controllers;

import com.ruppyrup.bigfun.server.EchoMultiServer;
import com.ruppyrup.bigfun.utils.Position;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

public class ServerController implements Initializable {

    private EchoMultiServer echoMultiServer;
    private final Map<String, Circle> players = new HashMap<>();
    private double ballPositionX;
    private double ballPositionY;
    private double dx = 3;
    private double dy = 3;

    @FXML
    private AnchorPane anchorPane;

    private Circle ball;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echoMultiServer = new EchoMultiServer(this);
        echoMultiServer.start();

        ball = createCircle(BALL_RADIUS, Color.ORANGE, new Position(100, 100));
        bounceBall();

        echoMultiServer.setOnSucceeded(event -> System.out.println("Succeeded :: " + echoMultiServer.getValue()));
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

        players.put(id, newPlayer);
    }

    public void removePlayer(String id) {
        Circle playerToRemove = players.get(id);
        playerToRemove.setDisable(true);
        playerToRemove.setVisible(false);
        players.remove(id);
    }

    private boolean hasPlayerHitBall(String id) {
        Circle player = players.get(id);
        double xValue = player.getCenterX();
        double yValue = player.getCenterY();
        return hasPlayerHitBall(xValue, yValue);
    }

    private boolean hasPlayerHitBall(double xValue, double yValue) {
        return ballPositionX - BALL_RADIUS <= xValue + PLAYER_RADIUS && ballPositionX + BALL_RADIUS >= xValue - PLAYER_RADIUS &&
                ballPositionY - BALL_RADIUS <= yValue + PLAYER_RADIUS && ballPositionY + BALL_RADIUS >= yValue + PLAYER_RADIUS;
    }

    private void bounceBall() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), t -> {
            //move the ball
            ballPositionX = ball.getCenterX() + dx;
            ballPositionY = ball.getCenterY() + dy;

            players.keySet().stream()
                    .filter(ServerController.this::hasPlayerHitBall)
                    .forEach(id -> {
                        System.out.println("Client hit by ball:: " + id);
                        dx *= -1;
                        dy *= -1;
                    });

            echoMultiServer.sendBallPosition(ballPositionX, ballPositionY);

            ball.setCenterX(ballPositionX);
            ball.setCenterY(ballPositionY);

            Bounds bounds = anchorPane.getLayoutBounds();
            final boolean atRightBorder = ball.getCenterX() >= (bounds.getMaxX() - ball.getRadius());
            final boolean atLeftBorder = ball.getCenterX() <= (bounds.getMinX() + ball.getRadius());
            final boolean atBottomBorder = ball.getCenterY() >= (bounds.getMaxY() - ball.getRadius());
            final boolean atTopBorder = ball.getCenterY() <= (bounds.getMinY() + ball.getRadius());

            if (atRightBorder || atLeftBorder) {
                dx *= -1;
            }
            if (atBottomBorder || atTopBorder) {
                dy *= -1;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void moveOtherPlayer(String id, double xValue, double yValue) {
        Circle playerToMove = players.get(id);
        if (playerToMove == null) return;

        if (hasPlayerHitBall(xValue, yValue)) {
            System.out.println("Ball has been hit by client :: " + id);
            dx *= -1;
            dy *= -1;
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
