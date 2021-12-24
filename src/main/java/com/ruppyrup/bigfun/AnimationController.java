package com.ruppyrup.bigfun;

import com.jfoenix.controls.JFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.util.Duration;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Stack;

public class AnimationController implements Initializable {

    private Queue<MouseEvent> mouseEvents = new LinkedList<>();

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
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(4));
        transition.setNode(button);
        transition.setToY(-200);
        transition.setToX(-100);

        transition.setAutoReverse(true);
        transition.setCycleCount(2);
        transition.play();

        transition.setOnFinished(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Completed");
            alert.show();
        });
//        button.setStyle("-fx-background-color: #00ff00");
    }

    @FXML
    void onMousePressed(MouseEvent event) {

        if (counter++ == 10) {
            mouseEvents.add(event);
            counter = 0;
        }

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

        System.out.println("button layout x ::" +button.getLayoutX());

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
        if (counter++ == 10) {
            mouseEvents.add(event);
            counter = 0;
        }
        buttonTransition();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
