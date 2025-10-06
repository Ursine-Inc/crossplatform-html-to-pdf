package com.ursineenterprises.utilities.htmltopdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("HTML to PDF Convertor");

        ToggleButton tb1 = new ToggleButton("toggle button 1");
        ToggleGroup group = new ToggleGroup();
        tb1.setToggleGroup(group);

        stage.setScene(scene);
        stage.show();
    }
}
