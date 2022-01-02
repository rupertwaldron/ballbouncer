package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.controllers.ClientController;
import javafx.application.Platform;

public class AddPlayer implements Command {

    private final ClientController animationController;
    private final String id;

    public AddPlayer(ClientController animationController, String id) {
        this.animationController = animationController;
        this.id = id;
    }

    @Override
    public void execute() {
        System.out.println("Executed add button");
        Platform.runLater(() -> animationController.addNewPlayer(id));
    }
}
