package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class AboutPage implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(AboutPage.class);

    @FXML
    public WebView _web;

    public static void display(Stage parent) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            InputStream layoutIS = Thread.currentThread().getContextClassLoader()
                                         .getResourceAsStream("com/qbcps/haw/AboutPage.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            stage.setTitle("About");
            stage.setScene(new Scene(root, 700, 500));
            stage.show();
        } catch (Exception e) {
            __l.error("Exception showing the About window", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (BufferedInputStream htmlStream = new BufferedInputStream(Thread.currentThread().getContextClassLoader()
                                                                            .getResourceAsStream("com/qbcps/haw/AboutPage.html"))) {
            String pageString = IOUtils.toString(htmlStream, "UTF-8");
            WebEngine engine = _web.getEngine();
            engine.loadContent(pageString);
        } catch (Exception e) {
            __l.error("Exception loading the About page.", e);
        }
    }
}
