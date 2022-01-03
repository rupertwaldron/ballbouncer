package com.ruppyrup.bigfun;

import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.controllers.ServerController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.FlowView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import io.datafx.controller.flow.Flow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;


class ServerApplicationTest extends ApplicationTest {
    ServerController controller;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Echo Server");
        stage.show();
    }

    @BeforeEach
    public void setUp () throws Exception {

    }

    @AfterEach
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        press(new MouseButton[]{});
    }

    @Test
    void testReset() {
        clickOn("#resetButton");

        Assertions.assertEquals(100, 100);

    }


}
