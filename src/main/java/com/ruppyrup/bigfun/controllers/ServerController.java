package com.ruppyrup.bigfun.controllers;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.server.EchoMultiServer;
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
import java.util.Random;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    private EchoMultiServer echoMultiServer;
    private Map<String, Button> players = new HashMap<>();

    @FXML
    private AnchorPane anchorPane;

    private Circle ball;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echoMultiServer = new EchoMultiServer(this);
        echoMultiServer.start();

        ball = new Circle(15, Color.PALEGREEN);
        ball.relocate(100, 100);
        anchorPane.getChildren().add(ball);
        bounceBall();

        echoMultiServer.setOnSucceeded(event -> {
            System.out.println("Succeeded :: " + echoMultiServer.getValue());
        });
    }

    public void addNewPlayer(String id) {
        System.out.println("Adding new button");
        Random random = new Random();
        String name = id.substring(15);
        String color = Integer.toHexString(random.nextInt(255))
                + Integer.toHexString(random.nextInt(255))
                + Integer.toHexString(random.nextInt(255));
        System.out.println("Color :: " + color);
        JFXButton newPlayerButton = new JFXButton(name);
        newPlayerButton.setStyle("-fx-background-color: #" + color +";-fx-background-radius: 2000");
        newPlayerButton.setMinSize(40, 40);
        newPlayerButton.setTextFill(Paint.valueOf("#FFFFFF"));
        newPlayerButton.setRipplerFill(Paint.valueOf("#FFFFFF"));
        newPlayerButton.setButtonType(JFXButton.ButtonType.RAISED);
        newPlayerButton.setLayoutX(random.nextDouble() * 400.0);
        newPlayerButton.setLayoutY(random.nextDouble() * 400.0);

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

    public void moveButton(String id, double xValue, double yValue) {

        Button buttonToMove = players.get(id);

        if (buttonToMove == null) return; // if own button or button doesn't exist

        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(300));
        transition.setNode(buttonToMove);

        double buttonX = buttonToMove.getLayoutX();
        double buttonY = buttonToMove.getLayoutY();

        double deltaX = xValue - buttonX - 20.0;
        double deltaY = yValue - buttonY - 20.0;

        transition.setToX(deltaX);
        transition.setToY(deltaY);
        transition.play();
    }


    private void bounceBall() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {

            double dx = 3; //Step on x or velocity
            double dy = 3; //Step on y

            @Override
            public void handle(ActionEvent t) {
                //move the ball
                double newXPosition = ball.getLayoutX() + dx;
                double newYPosition = ball.getLayoutY() + dy;

                echoMultiServer.sendBallPosition(newXPosition, newYPosition);

                ball.setLayoutX(newXPosition);
                ball.setLayoutY(newYPosition);

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
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
