package com.ruppyrup.bigfun.controllers;

import com.ruppyrup.bigfun.client.EchoClient;
import com.ruppyrup.bigfun.utils.Position;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

public class ClientController implements Initializable {
    private Queue<Position> mouseEvents = new LinkedList<>();
    private EchoClient echoClient;
    private Map<String, Circle> players = new HashMap<>();

    private int counter;

    @FXML
    private ImageView image;

    @FXML
    private AnchorPane anchorPane;

    private Circle ball;
    private Circle myPlayer;

    @FXML
    void onMouseMoved(MouseEvent event) {
        if (counter++ == 2) {
            double adjustedX = event.getX();
            double adjustedY = event.getY();
            mouseEvents.add(new Position(adjustedX, adjustedY));
            echoClient.sendMessage(adjustedX + ":" + adjustedY);
            counter = 0;
        }
        buttonTransition();
    }

    @FXML
    void onMousePressed(MouseEvent event) {
//        mouseEvents.add(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echoClient = new EchoClient(this, "127.0.0.1", 6666);
        echoClient.start();

        myPlayer = createCircle(PLAYER_RADIUS, Color.RED, new Position(200, 200));
        ball = createCircle(BALL_RADIUS, Color.ORANGE, new Position(100, 100));

        echoClient.setOnSucceeded(event -> System.out.println("Succeeded :: " + echoClient.getValue()));
    }

    private Circle createCircle(int radius, Paint color, Position startPosition) {
        Circle circle = new Circle(radius, color);
        circle.setCenterX(startPosition.getX());
        circle.setCenterY(startPosition.getY());
        anchorPane.getChildren().add(circle);
        return circle;
    }

    private void buttonTransition() {
        while (!mouseEvents.isEmpty()) {
            Position position = mouseEvents.remove();
            transitionNode(myPlayer, position.getX(), position.getY(), 150);
        }
    }

    public void addNewPlayer(String id) {
        System.out.println("Adding new button");
        String name = id.substring(15);
        String color = getRandomRGBColor();
        System.out.println("Color :: " + color);
        Circle newPlayer = createCircle(PLAYER_RADIUS,
                Color.valueOf(color),
                new Position(
                        getRandom().nextDouble() * 400.0,
                        getRandom().nextDouble() * 400.0));

        players.put(id, newPlayer);
    }

    public void moveOtherPlayer(String id, double xValue, double yValue) {
        Circle playerToMove = players.get(id);
        if (playerToMove == null) return; // if own button or button doesn't exist
        transitionNode(playerToMove, xValue, yValue, 150);
    }

    public void moveBall(Double xValue, Double yValue) {
        transitionNode(ball, xValue, yValue, 20);
    }

    private void transitionNode(Circle nodeToMove, double xValue, double yValue, int duration) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), t -> {
            nodeToMove.setCenterX(xValue);
            nodeToMove.setCenterY(yValue);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void removePlayer(String id) {
        Circle playerToRemove = players.get(id);
        playerToRemove.setDisable(true);
        playerToRemove.setVisible(false);
        players.remove(id);
    }
}
