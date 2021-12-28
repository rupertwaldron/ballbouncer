package com.ruppyrup.bigfun;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.client.EchoClient;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

    @FXML
    void onClick(ActionEvent event) {
        System.out.println("Clicked button");
        addNewButton("trev");
//        TranslateTransition transition = new TranslateTransition();
//        transition.setDuration(Duration.seconds(4));
//        transition.setNode(button);
//        transition.setToY(-200);
//        transition.setToX(-100);
//
//        transition.setAutoReverse(true);
//        transition.setCycleCount(2);
//        transition.play();
//
//        transition.setOnFinished(e -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText("Completed");
//            alert.show();
//        });
//        button.setStyle("-fx-background-color: #00ff00");
    }

    @FXML
    void onMousePressed(MouseEvent event) {

//        if (counter++ == 10) {
            mouseEvents.add(event);
//            counter = 0;
//        }
        String serverResponse = echoClient.sendMessage(event.getX() + ":" + event.getY());
        System.out.println(serverResponse);
        buttonTransition();

//        transition.setOnFinished(e -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText("Completed");
//            alert.show();
//        });
        System.out.println("X Value :: " + event.getX());
        System.out.println("Y Value :: " + event.getY());
    }

    private void buttonTransition() {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(300));
        transition.setNode(button);

        System.out.println("button layout x ::" + button.getLayoutX());

        while (!mouseEvents.isEmpty()) {

            MouseEvent event = mouseEvents.remove();

            double buttonX = button.getLayoutX();
            double buttonY = button.getLayoutY();

            double mouseX = event.getX();
            double mouseY = event.getY();

            double deltaX = mouseX - buttonX - 40.0;
            double deltaY = mouseY - buttonY - 40.0;

            transition.setToX(deltaX);
            transition.setToY(deltaY);

//        transition.setAutoReverse(true);
//        transition.setCycleCount(2);
            transition.play();
        }

    }


    @FXML
    void onMouseMoved(MouseEvent event) throws InterruptedException {
//        if (counter++ == 10) {
//            mouseEvents.add(event);
//            counter = 0;
//        }
//        buttonTransition();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echoClient = new EchoClient(this, "127.0.0.1", 6666);
        echoClient.start();
//        echoClient.getOtherClients();
        echoClient.setOnSucceeded(event -> {
            System.out.println("Succeeded :: " + echoClient.getValue());
        });
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

        System.out.println("button layout x ::" + buttonToMove.getLayoutX());

        double buttonX = buttonToMove.getLayoutX();
        double buttonY = buttonToMove.getLayoutY();

        double deltaX = xValue - buttonX - 20.0;
        double deltaY = yValue - buttonY - 20.0;

        transition.setToX(deltaX);
        transition.setToY(deltaY);
        transition.play();
    }
}
