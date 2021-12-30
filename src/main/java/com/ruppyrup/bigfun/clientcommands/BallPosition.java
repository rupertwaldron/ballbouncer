package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.controllers.ClientController;
import javafx.application.Platform;

public class BallPosition implements Command {

    private final ClientController animationController;
    private final String id;
    private final Double xValue;
    private final Double yValue;

    public BallPosition(ClientController clientController, String input) {
        String[] inputs = input.split("%");
        id = inputs[0];
        String[] xyValues = inputs[1].split(":");
        xValue = Double.valueOf(xyValues[0]);
        yValue = Double.valueOf(xyValues[1]);
        this.animationController = clientController;
    }

    @Override
    public void execute() {
        Platform.runLater(() -> animationController.moveBall(xValue, yValue));
        System.out.println("Executed BallPosition to coordinate: " + xValue + ":" + yValue);
    }
}
