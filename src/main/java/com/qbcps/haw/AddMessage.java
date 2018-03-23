package com.qbcps.haw;
/*
 * Copyright 3/23/18 by Stephen Beitzel
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.dumbster.smtp.eml.EMLMailMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class AddMessage implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(AddMessage.class);

    private ServerInstance _servers;
    private Stage _stage;

    @FXML public TextArea _messageArea;

    public static void display(Stage parent, ServerInstance server) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            InputStream layoutIS = Thread.currentThread().getContextClassLoader()
                                         .getResourceAsStream("com/qbcps/haw/AddMessage.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            AddMessage am = loader.getController();
            am._servers = server;
            am._stage = stage;
            stage.setTitle("Add Message");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            __l.error("Exception showing the Add Message window", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    @SuppressWarnings("unused")
    public void handleAdd(ActionEvent evt) {
        try {
            String rawMessage = _messageArea.getText();
            ByteArrayInputStream bis = new ByteArrayInputStream(rawMessage.getBytes("UTF-8"));
            EMLMailMessage dumbsterMessage = new EMLMailMessage(bis);
            _servers.receiveMessage(dumbsterMessage);
            _stage.close();
        } catch (Exception e) {
            __l.error("Exception adding message to store!", e);
        }
    }
}
