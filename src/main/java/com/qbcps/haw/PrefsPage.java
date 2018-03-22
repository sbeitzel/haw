package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.io.InputStream;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class PrefsPage {
    private static final Logger __l = LoggerFactory.getLogger(PrefsPage.class);
    private static final Preferences PREFS = Preferences.userNodeForPackage(PrefsPage.class);

    private static final String PKEY_WINDOW_HEIGHT = "window.height";
    private static final String PKEY_WINDOW_WIDTH = "window.width";
    private static final double W_H_DEF = 600;
    private static final double W_W_DEF = 700;

    private static final String PKEY_CURRENT_SERVERS = "services";

    public static double getMainWindowWidth() {
        return PREFS.getDouble(PKEY_WINDOW_WIDTH, W_W_DEF);
    }

    public static void setMainWindowWidth(double newValue) {
        PREFS.putDouble(PKEY_WINDOW_WIDTH, newValue);
    }

    public static double getMainWindowHeight() {
        return PREFS.getDouble(PKEY_WINDOW_HEIGHT, W_H_DEF);
    }

    public static void setMainWindowHeight(double newValue) {
        PREFS.putDouble(PKEY_WINDOW_HEIGHT, newValue);
    }

    public static void setCurrentServerConfigs(String json) {
        PREFS.put(PKEY_CURRENT_SERVERS, json);
    }

    public static String getCurrentServerConfigs() {
        return PREFS.get(PKEY_CURRENT_SERVERS, "[]");
    }

    public static void display(Stage parent) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            InputStream layoutIS = Thread.currentThread().getContextClassLoader()
                                         .getResourceAsStream("com/qbcps/haw/PrefsPage.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            stage.setTitle("Preferences");
            stage.setScene(new Scene(root, 700, 500));
            stage.show();
        } catch (Exception e) {
            __l.error("Exception showing the prefs window", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleResetWindowPrefs(ActionEvent evt) {
        setMainWindowWidth(W_W_DEF);
        setMainWindowHeight(W_H_DEF);
    }
}
