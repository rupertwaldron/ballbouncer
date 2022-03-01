package com.ruppyrup.bigfun;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;


class ServerApplicationTest extends ApplicationTest {

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
