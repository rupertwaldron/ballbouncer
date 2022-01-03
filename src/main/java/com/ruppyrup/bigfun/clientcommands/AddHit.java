package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.controllers.ClientController;
import javafx.application.Platform;

public class AddHit implements Command{

    private final ClientController animationController;
    private final String count;

    public AddHit(ClientController animationController, String count) {
        this.animationController = animationController;
        this.count = count;
    }

    @Override
    public void execute() {
        System.out.println("Executed add hit count");
        Platform.runLater(() -> animationController.updateHitCount(count));
    }
}
