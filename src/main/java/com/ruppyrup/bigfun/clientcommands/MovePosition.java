package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.AnimationController;
import com.ruppyrup.bigfun.Command;
import javafx.application.Platform;

public class MovePosition implements Command {

    private final AnimationController animationController;
    private final String id;
    private final Double xValue;
    private final Double yValue;

    public MovePosition(AnimationController animationController, String input) {
        String[] inputs = input.split("%");
        id = inputs[0];
        String[] xyValues = inputs[1].split(":");
        xValue = Double.valueOf(xyValues[0]);
        yValue = Double.valueOf(xyValues[1]);
        this.animationController = animationController;
    }

    @Override
    public void execute() {
        Platform.runLater(() -> animationController.moveButton(id, xValue, yValue));
        System.out.println("Executed MovePosition to coordinate: " + xValue + ":" + yValue);
    }
}
