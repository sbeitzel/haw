package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the JavaFX main entry point.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Main extends Application {
    private static final Logger __l = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // read configuration
            // read preferences
            // display the main window
            InputStream layoutIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/qbcps/haw/Dashboard.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            Dashboard d = loader.getController();
            d.setStage(primaryStage);

            primaryStage.setTitle("haw - a simple email server for local testing"); // TODO get this string from one place
            primaryStage.setScene(new Scene(root, PrefsPage.getMainWindowWidth(), PrefsPage.getMainWindowHeight()));
            primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> PrefsPage.setMainWindowWidth(newValue.doubleValue()));
            primaryStage.heightProperty().addListener(((observable, oldValue, newValue) -> PrefsPage.setMainWindowHeight(newValue.doubleValue())));
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> Platform.exit());
        } catch (Exception e) {
            __l.error("Exception starting application!", e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        // write preferences
        // stop servers
    }
}
