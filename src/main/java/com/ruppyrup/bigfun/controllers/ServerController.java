package com.ruppyrup.bigfun.controllers;

import com.ruppyrup.bigfun.server.EchoMultiServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    private EchoMultiServer echoMultiServer;

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
