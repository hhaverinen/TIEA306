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

package main.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Created by Henri on 18.3.2016.
 */
public class AliasWindowController {

    @FXML
    private ComboBox driverBox;

    @FXML
    private TextField urlField, userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void saveConnection(ActionEvent event) {
        messageLabel.setText("");

        String driverBoxSelection = (String) driverBox.getSelectionModel().getSelectedItem();
        String urlFieldText = urlField.getText();
        String userFieldText = userField.getText();
        String passwordFieldText = passwordField.getText();

        if (driverBoxSelection != null && !urlFieldText.isEmpty() && !userFieldText.isEmpty() && !passwordFieldText.isEmpty()) {
            ConnectionPOJO cpojo = new ConnectionPOJO(driverBoxSelection, userFieldText, passwordFieldText, urlFieldText);
            Context.getInstance().getConnections().add(cpojo);

        } else {
            messageLabel.setText("Some information is missing!");
        }
    }

}
