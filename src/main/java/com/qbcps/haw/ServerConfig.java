package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.qbcps.haw.ServerInstance.PROP_MAILSTORE_CLASS;
import static com.qbcps.haw.ServerInstance.PROP_MAXSIZE;
import static com.qbcps.haw.ServerInstance.PROP_NULL_SETTING;
import static com.qbcps.haw.ServerInstance.PROP_NUM_POP3_THREADS;
import static com.qbcps.haw.ServerInstance.PROP_NUM_SMTP_THREADS;
import static com.qbcps.haw.ServerInstance.PROP_POP3_PORT;
import static com.qbcps.haw.ServerInstance.PROP_SERVER_SOCKET_TIMEOUT;
import static com.qbcps.haw.ServerInstance.PROP_SMTP_PORT;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class ServerConfig implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(ServerConfig.class);
    private static final Map<String, String> MAIL_STORES = new ConcurrentHashMap<>();
    private static final Map<String, BiConsumer<Label, TextField>> STORE_SETTINGS = new ConcurrentHashMap<>();
    private static final Map<String, String> STORE_SETTING_PROP_KEY = new ConcurrentHashMap<>();

    static {
        final String key_EML = "EML File";
        final String key_Fixed = "Fixed Size";
        final String key_Rolling = "Rolling";
        final String key_Null = "Null";

        MAIL_STORES.put(key_EML, "com.dumbster.smtp.mailstores.EMLMailStore");
        STORE_SETTING_PROP_KEY.put(key_EML, PROP_NULL_SETTING);
        MAIL_STORES.put(key_Fixed, "com.dumbster.smtp.mailstores.FixedSizeMailStore");
        STORE_SETTING_PROP_KEY.put(key_Fixed, PROP_MAXSIZE);
        MAIL_STORES.put(key_Rolling, "com.dumbster.smtp.mailstores.RollingMailStore");
        STORE_SETTING_PROP_KEY.put(key_Rolling, PROP_NULL_SETTING);
        MAIL_STORES.put(key_Null, "com.dumbster.smtp.mailstores.NullMailStore");
        STORE_SETTING_PROP_KEY.put(key_Null, PROP_NULL_SETTING);

        STORE_SETTINGS.put(key_EML, (label, field) -> {
            label.setText("File name");
            field.setPromptText("path to EML file");
            field.setText("");
            field.setDisable(false);
            field.setVisible(true);
        });
        STORE_SETTINGS.put(key_Fixed, (label, field) -> {
            label.setText("Store size");
            field.setPromptText("message store size");
            field.setText("");
            field.setDisable(false);
            field.setVisible(true);
        });
        STORE_SETTINGS.put(key_Rolling, (label, field) -> {
            label.setText("No settings");
            field.setPromptText("");
            field.setText("");
            field.setDisable(true);
            field.setVisible(false);
        });
        STORE_SETTINGS.put(key_Null, (label, field) -> {
            label.setText("No settings");
            field.setPromptText("");
            field.setText("");
            field.setDisable(true);
            field.setVisible(false);
        });
    }

    @FXML public TextField _smtpPort;
    @FXML public TextField _smtpThreads;
    @FXML public TextField _popPort;
    @FXML public TextField _popThreads;
    @FXML public ChoiceBox<String> _mailStoreClass;
    @FXML public Label _storePrefLabel;
    @FXML public TextField _storeSetting;
    @FXML public TextField _socketTimeout;

    private Stage _stage;
    private Consumer<ServerInstance> _serverInstanceConsumer;

    @SuppressWarnings("unused")
    public static void display(Stage parent, Consumer<ServerInstance> serverInstanceConsumer) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            InputStream layoutIS = Thread.currentThread().getContextClassLoader()
                                         .getResourceAsStream("com/qbcps/haw/ServerConfig.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            ServerConfig sc = loader.getController();
            sc._stage = stage;
            sc._serverInstanceConsumer = serverInstanceConsumer;
            stage.setTitle("New Service Configuration");
            stage.setScene(new Scene(root, 700, 500));
            stage.show();
        } catch (Exception e) {
            __l.error("Exception showing the service config window", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // populate the choicebox with mailstore implementations
        ArrayList<String> storeNames = new ArrayList<>(MAIL_STORES.keySet());
        Collections.sort(storeNames);
        _mailStoreClass.setItems(FXCollections.observableArrayList(storeNames));
        _mailStoreClass.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                STORE_SETTINGS.get(newValue).accept(_storePrefLabel, _storeSetting);
            } catch (Exception e) {
                __l.error("UI Exception! Probably the static initializer block is broken", e);
            }
        });
        _mailStoreClass.getSelectionModel().select(1);
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleCancel(ActionEvent evt) {
        _stage.close();
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleOK(ActionEvent event) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            MapConfiguration config = new MapConfiguration(params);
            config.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

            config.addProperty(PROP_NUM_SMTP_THREADS, _smtpThreads.getText());
            config.addProperty(PROP_NUM_POP3_THREADS, _popThreads.getText());
            config.addProperty(PROP_SMTP_PORT, _smtpPort.getText());
            config.addProperty(PROP_POP3_PORT, _popPort.getText());
            config.addProperty(PROP_MAILSTORE_CLASS, MAIL_STORES.get(_mailStoreClass.getValue()));
            config.addProperty(PROP_SERVER_SOCKET_TIMEOUT, _socketTimeout.getText());
            config.addProperty(STORE_SETTING_PROP_KEY.get(_mailStoreClass.getValue()), _storeSetting.getText());

            _serverInstanceConsumer.accept(ServerInstance.startService(config));
        } catch (Exception e) {
            __l.error("Exception instantiating or starting the service", e);
        } finally {
            _stage.close();
        }
    }
}
