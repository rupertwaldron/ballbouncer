package com.ruppyrup.bigfun;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.client.EchoClient;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
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
import java.util.Random;
import java.util.ResourceBundle;

public class AnimationController implements Initializable {

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
    void onMousePressed(MouseEvent event) {
//        mouseEvents.add(event);
        String serverResponse = echoClient.sendMessage(event.getX() + ":" + event.getY());
        System.out.println(serverResponse);
        buttonTransition();

        System.out.println("X Value :: " + event.getX());
        System.out.println("Y Value :: " + event.getY());
    }

    private void buttonTransition() {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(300));
        transition.setNode(button);

        while (!mouseEvents.isEmpty()) {

            MouseEvent event = mouseEvents.remove();

            double buttonX = button.getLayoutX();
            double buttonY = button.getLayoutY();

            double mouseX = event.getX();
            double mouseY = event.getY();

            double deltaX = mouseX - buttonX - 20.0;
            double deltaY = mouseY - buttonY - 20.0;

            transition.setToX(deltaX);
            transition.setToY(deltaY);
            transition.play();
        }
    }


    @FXML
    void onMouseMoved(MouseEvent event) throws InterruptedException {
        if (counter++ == 10) {
            mouseEvents.add(event);
            echoClient.sendMessage(event.getX() + ":" + event.getY());
            counter = 0;
        }
        buttonTransition();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echoClient = new EchoClient(this, "127.0.0.1", 6666);
        echoClient.start();
//        echoClient.getOtherClients();

        ball = new Circle(15, Color.BLUE);
        ball.relocate(100, 100);
        anchorPane.getChildren().add(ball);
        bounceBall();

        echoClient.setOnSucceeded(event -> {
            System.out.println("Succeeded :: " + echoClient.getValue());
        });
    }

    private void bounceBall() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {

            double dx = 3; //Step on x or velocity
            double dy = 3; //Step on y

            @Override
            public void handle(ActionEvent t) {
                //move the ball
                ball.setLayoutX(ball.getLayoutX() + dx);
                ball.setLayoutY(ball.getLayoutY() + dy);

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

    public void addNewButton(String id) {
        System.out.println("Adding new button");
        Random random = new Random();
        String name = id.substring(15);
        String color = Integer.toHexString(random.nextInt(255))
                + Integer.toHexString(random.nextInt(255))
                + Integer.toHexString(random.nextInt(255));
        System.out.println("Color :: " + color);
        JFXButton friendButton = new JFXButton(name);
        friendButton.setStyle("-fx-background-color: #" + color +";-fx-background-radius: 2000");
        friendButton.setMinSize(40, 40);
        friendButton.setTextFill(Paint.valueOf("#FFFFFF"));
        friendButton.setRipplerFill(Paint.valueOf("#FFFFFF"));
        friendButton.setButtonType(JFXButton.ButtonType.RAISED);
        friendButton.setLayoutX(random.nextDouble() * 400.0);
        friendButton.setLayoutY(random.nextDouble() * 400.0);

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
}
