package com.ruppyrup.bigfun.controllers;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.client.EchoClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

public class ClientController implements Initializable {
    private Queue<MouseEvent> mouseEvents = new LinkedList<>();
    private EchoClient echoClient;
    private Map<String, Button> buttons = new HashMap<>();

    private int counter;

    @FXML
    private JFXButton button;

    @FXML
    private ImageView image;

    @FXML
    private AnchorPane anchorPane;

    private Circle ball;


    @FXML
    void onbuttonPressed(ActionEvent event) {

//        bounceBall();
    }

    @FXML
    void onMouseMoved(MouseEvent event) throws InterruptedException {
        if (counter++ == 2) {
            mouseEvents.add(event);
            echoClient.sendMessage(event.getX()+ ":" + event.getY());
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

        ball = new Circle(15, Color.LIGHTCYAN);
        ball.relocate(100, 100);
        anchorPane.getChildren().add(ball);

        echoClient.setOnSucceeded(event -> System.out.println("Succeeded :: " + echoClient.getValue()));
    }



    private void buttonTransition() {
        while (!mouseEvents.isEmpty()) {
            MouseEvent event = mouseEvents.remove();
            transitionNode(button, event.getX(), event.getY(), 150);
        }
    }

    public void addNewButton(String id) {
        System.out.println("Adding new button");
        String name = id.substring(15);
        String color = getRandomRGBColor();
        System.out.println("Color :: " + color);
        JFXButton friendButton = new JFXButton(name);
        friendButton.setStyle("-fx-background-color: #" + color +";-fx-background-radius: 2000");
        friendButton.setMinSize(40, 40);
        friendButton.setTextFill(Paint.valueOf("#FFFFFF"));
        friendButton.setRipplerFill(Paint.valueOf("#FFFFFF"));
        friendButton.setButtonType(JFXButton.ButtonType.RAISED);
        friendButton.setLayoutX(getRandom().nextDouble() * 400.0);
        friendButton.setLayoutY(getRandom().nextDouble() * 400.0);

        buttons.put(id, friendButton);

        try {
            anchorPane.getChildren().add(friendButton);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Added button finished");
        }
    }

    public void moveButton(String id, double xValue, double yValue) {
        Button buttonToMove = buttons.get(id);
        if (buttonToMove == null) return; // if own button or button doesn't exist
        transitionNode(buttonToMove, xValue, yValue, 150);
    }


    public void moveBall(Double xValue, Double yValue) {
        transitionNode(ball, xValue, yValue, 20);
    }

    private void transitionNode(Node nodeToMove, double xValue, double yValue, int duration) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), t -> {
            nodeToMove.setLayoutX(xValue);
            nodeToMove.setLayoutY(yValue);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void removePlayer(String id) {
        Button buttonToRemove = buttons.get(id);
        buttonToRemove.setDisable(true);
        buttonToRemove.setVisible(false);
        buttons.remove(id);
    }
}
