package com.ruppyrup.bigfun.controllers;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.server.EchoMultiServer;
import com.ruppyrup.bigfun.utils.CommonUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.BUTTON_DIAMETER;
import static com.ruppyrup.bigfun.constants.BallConstants.BUTTON_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;

public class ServerController implements Initializable {

    private EchoMultiServer echoMultiServer;
    private final Map<String, Button> players = new HashMap<>();
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

        ball = new Circle(BALL_RADIUS, Color.ORANGE);
        ball.relocate(100, 100);
        anchorPane.getChildren().add(ball);
        bounceBall();

        echoMultiServer.setOnSucceeded(event -> {
            System.out.println("Succeeded :: " + echoMultiServer.getValue());
        });
    }

    public void addNewPlayer(String id) {
        System.out.println("Adding new button");
        String name = id.substring(15);
        String color = CommonUtil.getRandomRGBColor();
        System.out.println("Color :: " + color);
        JFXButton newPlayerButton = new JFXButton(name);
        newPlayerButton.setStyle("-fx-background-color: #" + color +";-fx-background-radius: 2000");
        newPlayerButton.setMinSize(BUTTON_DIAMETER, BUTTON_DIAMETER);
        newPlayerButton.setTextFill(Paint.valueOf("#FFFFFF"));
        newPlayerButton.setRipplerFill(Paint.valueOf("#FFFFFF"));
        newPlayerButton.setButtonType(JFXButton.ButtonType.RAISED);
        newPlayerButton.setLayoutX(getRandom().nextDouble() * 400.0);
        newPlayerButton.setLayoutY(getRandom().nextDouble() * 400.0);

        players.put(id, newPlayerButton);

        try {
            anchorPane.getChildren().add(newPlayerButton);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Added player finished");
        }
    }

    public void removePlayer(String id) {
        Button buttonToRemove = players.get(id);
        buttonToRemove.setDisable(true);
        buttonToRemove.setVisible(false);
        players.remove(id);
    }

    private boolean hasPlayerHitBall(String id) {
        Button button = players.get(id);
        double xValue = button.getLayoutX();
        double yValue = button.getLayoutY();
        return hasPlayerHitBall(xValue, yValue);
    }

    private boolean hasPlayerHitBall(double xValue, double yValue) {
        return ballPositionX - BALL_RADIUS <= xValue + BUTTON_RADIUS && ballPositionX + BALL_RADIUS >= xValue - BUTTON_RADIUS &&
                ballPositionY - BALL_RADIUS <= yValue + BUTTON_RADIUS && ballPositionY + BALL_RADIUS >= yValue + BUTTON_RADIUS;
    }



    private void bounceBall() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), t -> {
            //move the ball
            ballPositionX = ball.getLayoutX() + dx;
            ballPositionY = ball.getLayoutY() + dy;

            players.keySet().stream()
                    .filter(ServerController.this::hasPlayerHitBall)
                    .forEach(id -> {
                        System.out.println("Client hit by ball:: " + id);
                        dx *= -1;
                        dy *= -1;
                    });

            echoMultiServer.sendBallPosition(ballPositionX, ballPositionY);

            ball.setLayoutX(ballPositionX);
            ball.setLayoutY(ballPositionY);

            Bounds bounds = anchorPane.getLayoutBounds();
            final boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
            final boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
            final boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius());
            final boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius());

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

    public void moveButton(String id, double xValue, double yValue) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), t -> {
            Button buttonToMove = players.get(id);

            if (buttonToMove == null) return; // if own button or button doesn't exist


            if (hasPlayerHitBall(xValue, yValue)) {
                System.out.println("Ball has been hit by client :: " + id);
                dx *= -1;
                dy *= -1;
            }

            buttonToMove.setLayoutX(xValue);
            buttonToMove.setLayoutY(yValue);

            System.out.println("Moving button to x y ::" + buttonToMove.getLayoutX() + " : " + buttonToMove.getLayoutY());
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
