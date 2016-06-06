/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Henri Haverinen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import main.java.helper.FileHelper;
import main.java.model.ConnectionPOJO;
import main.java.model.Context;
import main.java.model.DriverPOJO;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Henri on 18.3.2016.
 */
public class AliasWindowController implements Initializable {

    @FXML
    public ComboBox driverBox, aliasBox;

    @FXML
    private TextField aliasNameField, urlField, userField;

    @FXML
    private PasswordField passwordField;

    /**
     * makes bindings between pojo lists and combo boxes
     * @param url not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // make bindings
        driverBox.itemsProperty().bind(Context.getInstance().getDrivers());
        aliasBox.itemsProperty().bind(Context.getInstance().getConnections());
    }

    /**
     * adds a new alias
     * @param event not used
     */
    @FXML
    private void addAlias(ActionEvent event) {
        String aliasNameFieldText = aliasNameField.getText();
        DriverPOJO driverBoxSelection = (DriverPOJO) driverBox.getSelectionModel().getSelectedItem();
        String urlFieldText = urlField.getText();
        String userFieldText = userField.getText();
        String passwordFieldText = passwordField.getText();

        if (checkFields()) {
            ConnectionPOJO cpojo = new ConnectionPOJO(aliasNameFieldText, driverBoxSelection, userFieldText, passwordFieldText, urlFieldText);
            Context.getInstance().getConnections().get().add(cpojo);
            FileHelper helper = new FileHelper();
            List<ConnectionPOJO> pojos =  Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
            try {
                helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
                aliasBox.getSelectionModel().select(cpojo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * modifies existing alias
     * @param event not used
     */
    @FXML
    private void modifyAlias(ActionEvent event) {
        ConnectionPOJO connectionPOJO = (ConnectionPOJO) aliasBox.getSelectionModel().getSelectedItem();

        if (connectionPOJO != null && checkFields()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Modify confirmation");
            alert.setHeaderText("Are you sure you want to modify alias '" + connectionPOJO.getName() + "'");
            alert.setContentText("If you are, just hit the OK!");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                connectionPOJO.setName(aliasNameField.getText());
                connectionPOJO.setDatabaseUrl(urlField.getText());
                connectionPOJO.setDriver(((DriverPOJO) driverBox.getSelectionModel().getSelectedItem()));
                connectionPOJO.setUsername(userField.getText());
                connectionPOJO.setPassword(userField.getText());

                FileHelper helper = new FileHelper();
                List<ConnectionPOJO> pojos =  Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
                try {
                    helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * deletes existing alias
     * @param event
     */
    @FXML
    private void deleteAlias(ActionEvent event) {
        ConnectionPOJO connectionPOJO = (ConnectionPOJO) aliasBox.getSelectionModel().getSelectedItem();

        if (connectionPOJO != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText("Are you sure you want to delete alias '" + connectionPOJO.getName() + "'");
            alert.setContentText("If you are, just hit the OK!");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                Context.getInstance().getConnections().get().remove(connectionPOJO);
                FileHelper helper = new FileHelper();
                List<ConnectionPOJO> pojos =  Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
                try {
                    helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * loads alias's data to field when user selects it from the combo box
     * @param event
     */
    @FXML
    private void selectAlias(ActionEvent event) {
        ConnectionPOJO connectionPOJO = (ConnectionPOJO) aliasBox.getSelectionModel().getSelectedItem();
        aliasNameField.setText(connectionPOJO.getName());
        driverBox.getSelectionModel().select(connectionPOJO.getDriver()); // FIXME does not select correct object
        urlField.setText(connectionPOJO.getDatabaseUrl());
        userField.setText(connectionPOJO.getUsername());
        passwordField.setText(connectionPOJO.getPassword());
    }

    /**
     * checks that all fields are filled
     * @return true if everything is filled, false otherwise
     */
    private boolean checkFields() {
        return (!aliasNameField.getText().isEmpty() && driverBox.getSelectionModel().getSelectedItem()
                != null && !urlField.getText().isEmpty() && !userField.getText().isEmpty()
                && !passwordField.getText().isEmpty());
    }

}
