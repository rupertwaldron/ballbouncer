package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.AnimationController;
import com.ruppyrup.bigfun.Command;
import javafx.application.Platform;

public class AddPlayer implements Command {

    private final AnimationController animationController;
    private final String id;

    public AddPlayer(AnimationController animationController, String id) {
        this.animationController = animationController;
        this.id = id;
    }

    @Override
    public void execute() {
        System.out.println("Executed add button");
        Platform.runLater(() -> animationController.addNewButton(id));
    }
}
