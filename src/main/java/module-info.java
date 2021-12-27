module com.ruppyrup.bigfun {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.ruppyrup.bigfun to javafx.fxml;
    exports com.ruppyrup.bigfun;
    exports com.ruppyrup.bigfun.clientcommands;
    opens com.ruppyrup.bigfun.clientcommands to javafx.fxml;
}
