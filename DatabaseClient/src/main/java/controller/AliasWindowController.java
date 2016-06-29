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

package controller;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import component.PojoComboBox;
import helper.FileHelper;
import model.ConnectionPOJO;
import model.Context;
import model.DriverPOJO;

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
    public PojoComboBox driverBox, aliasBox;

    @FXML
    private TextField aliasNameField, urlField, userField;

    @FXML
    private PasswordField passwordField;

    private static final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    /**
     * makes bindings between pojo lists and combo boxes
     *
     * @param url            not used
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
     *
     * @param event not used
     */
    @FXML
    private void addAlias(ActionEvent event) {
        String aliasNameFieldText = aliasNameField.getText();
        DriverPOJO driverBoxSelection = (DriverPOJO) driverBox.getSelectionModel().getSelectedItem();
        String urlFieldText = urlField.getText();
        String userFieldText = userField.getText();
        String passwordFieldText = passwordField.getText();

        if (validateFields()) {
            ConnectionPOJO cpojo = new ConnectionPOJO(aliasNameFieldText, driverBoxSelection, userFieldText, passwordFieldText, urlFieldText);
            Context.getInstance().getConnections().get().add(cpojo);
            FileHelper helper = new FileHelper();
            List<ConnectionPOJO> pojos = Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
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
     *
     * @param event not used
     */
    @FXML
    private void modifyAlias(ActionEvent event) {
        if (validateComboBox(aliasBox)) {
            ConnectionPOJO connectionPOJO = (ConnectionPOJO) aliasBox.getSelectionModel().getSelectedItem();

            if (connectionPOJO != null && validateFields()) {
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
                    connectionPOJO.setPassword(passwordField.getText());

                    FileHelper helper = new FileHelper();
                    List<ConnectionPOJO> pojos = Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
                    try {
                        helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * deletes existing alias
     *
     * @param event not used
     */
    @FXML
    private void deleteAlias(ActionEvent event) {
        if(validateComboBox(aliasBox)) {
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
                    List<ConnectionPOJO> pojos = Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
                    try {
                        helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * loads alias's data to field when user selects it from the combo box
     *
     * @param event not used
     */
    @FXML
    private void selectAlias(ActionEvent event) {
        ConnectionPOJO connectionPOJO = (ConnectionPOJO) aliasBox.getSelectionModel().getSelectedItem();
        aliasNameField.setText(connectionPOJO.getName());
        driverBox.getSelectionModel().select(connectionPOJO.getDriver());
        urlField.setText(connectionPOJO.getDatabaseUrl());
        userField.setText(connectionPOJO.getUsername());
        passwordField.setText(connectionPOJO.getPassword());
    }

    /**
     * checks that all fields are filled
     *
     * @return true if everything is filled, false otherwise
     */
    private boolean validateFields() {
        // do it like this, so that every field gets validated and assigned correct pseudoclass
        boolean isAliasOk = validateTextField(aliasNameField);
        boolean isDriverOk = validateComboBox(driverBox);
        boolean isUrlOk = validateTextField(urlField);
        boolean isUserOk = validateTextField(userField);
        boolean isPasswordOk = validateTextField(passwordField);

        return (isAliasOk && isDriverOk && isUrlOk && isUserOk && isPasswordOk);
    }

    /**
     * checks if textfield is empty and sets error class depending of result
     *
     * @param textField textfield to validate
     * @return true if textField is not empty, false otherwise
     */
    private boolean validateTextField(TextField textField) {
        if (!textField.getText().isEmpty()) {
            textField.pseudoClassStateChanged(errorClass, false);
            return true;
        } else {
            textField.pseudoClassStateChanged(errorClass, true);
            return false;
        }
    }

    private boolean validateComboBox(ComboBox comboBox) {
        if(comboBox.getSelectionModel().getSelectedItem() != null) {
            comboBox.pseudoClassStateChanged(errorClass, false);
            return true;
        } else {
            comboBox.pseudoClassStateChanged(errorClass, true);
            return false;
        }
    }

}
