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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.helper.FileHelper;
import main.java.model.ConnectionPOJO;
import main.java.model.Context;
import main.java.model.DriverPOJO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Henri on 18.3.2016.
 */
public class AliasWindowController {

    @FXML
    public ComboBox driverBox;

    @FXML
    private TextField aliasNameField, urlField, userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void saveConnection(ActionEvent event) {
        messageLabel.setText("");

        // for testing
        String aliasNameFieldText = aliasNameField.getText();
        DriverPOJO driverBoxSelection = (DriverPOJO) driverBox.getSelectionModel().getSelectedItem();
        String urlFieldText = urlField.getText();
        String userFieldText = userField.getText();
        String passwordFieldText = passwordField.getText();

        if (!aliasNameFieldText.isEmpty() && driverBoxSelection != null && !urlFieldText.isEmpty() && !userFieldText.isEmpty() && !passwordFieldText.isEmpty()) {
            ConnectionPOJO cpojo = new ConnectionPOJO(aliasNameFieldText, driverBoxSelection.getDriverName(), userFieldText, passwordFieldText, urlFieldText);
            Context.getInstance().getConnections().get().add(cpojo);
            FileHelper helper = new FileHelper();
            List<ConnectionPOJO> pojos =  Context.getInstance().getConnections().get().stream().collect(Collectors.toList());
            try {
                helper.writeObjectsToJsonFile(pojos, "conf/aliases.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Please fill all the fields");
        }
    }

}
