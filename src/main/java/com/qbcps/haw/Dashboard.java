package com.qbcps.haw;
        /*
         * Copyright 3/20/18 by Stephen Beitzel
         */

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Dashboard implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(Dashboard.class);

    private Stage _stage;
    private ObservableList<ServerInstance> _servers;

    @FXML public MenuBar _mBar;
    @FXML public TableView<ServerInstance> _serverTable;
    @FXML public TableColumn<ServerInstance, Integer> _smtpPort;
    @FXML public TableColumn<ServerInstance, Integer> _popPort;
    @FXML public TableColumn<ServerInstance, Integer> _messageCount;
    @FXML public TableColumn<ServerInstance, Integer> _receivedCount;
    @FXML public Button _newButton;
    @FXML public Button _stopButton;

    @Override
    @SuppressWarnings("unused")
    public void initialize(URL location, ResourceBundle resources) {
        _servers = FXCollections.observableList(new ArrayList<>());
        _serverTable.setItems(_servers);
        _smtpPort.setCellValueFactory(new PropertyValueFactory<>("smtpPort"));
        _popPort.setCellValueFactory(new PropertyValueFactory<>("popPort"));
        _messageCount.setCellValueFactory(new PropertyValueFactory<>("messageCount"));
        _receivedCount.setCellValueFactory(new PropertyValueFactory<>("totalReceived"));

        _serverTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> _stopButton.setDisable(newValue == null));
    }

    void setStage(Stage stage) {
        _stage = stage;
    }

    void stopAll() {
        for (ServerInstance s : _servers) {
            s.stop();
        }
        _servers.clear();
    }

    String getCurrentServerInstanceConfigs() {
        JSONArray instances = new JSONArray();
        for (ServerInstance si : _servers) {
            instances.put(si.getParameters());
        }
        return instances.toString();
    }

    void startServersFromConfigs(String json) {
        JSONArray servers = new JSONArray(json);
        for (Object sc : servers) {
            if (sc instanceof JSONObject) {
                _servers.add(ServerInstance.startService((JSONObject) sc));
            } else {
                __l.warn("JSONArray contained a thing that isn't a JSONObject");
            }
        }
    }

    @SuppressWarnings("unused")
    @FXML
    public void handlePreferences(ActionEvent evt) {
        PrefsPage.display(_stage);
    }

    @SuppressWarnings("unused")
    @FXML
    public void handleAbout(ActionEvent evt) {
        AboutPage.display(_stage);
    }

    @SuppressWarnings("unused")
    @FXML
    public void handleQuit(ActionEvent evt) {
        Platform.exit();
    }

    @SuppressWarnings("unused")
    @FXML
    public void handleNew(ActionEvent evt) {
        ServerConfig.display(_stage, (si) -> _servers.add(si));
    }

    @SuppressWarnings("unused")
    @FXML
    public void handleStop(ActionEvent evt) {
        ServerInstance s = _serverTable.getSelectionModel().getSelectedItem();
        if (s != null) {
            s.stop();
            _servers.remove(s);
        }
    }
}
