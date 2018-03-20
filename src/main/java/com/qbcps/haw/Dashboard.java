package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Dashboard implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(Dashboard.class);

    private Stage _stage;

    @FXML public MenuBar _mBar;


    @Override
    @SuppressWarnings("unused")
    public void initialize(URL location, ResourceBundle resources) {

    }

    void setStage(Stage stage) {
        _stage = stage;
    }

    @SuppressWarnings("unused")
    public void handlePreferences(ActionEvent evt) {
        PrefsPage.display(_stage);
    }

    @SuppressWarnings("unused")
    public void handleAbout(ActionEvent evt) {
        AboutPage.display(_stage);
    }

    @SuppressWarnings("unused")
    public void handleQuit(ActionEvent evt) {
        Platform.exit();
    }
}
