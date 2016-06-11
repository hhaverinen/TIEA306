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

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.helper.FileHelper;
import main.java.model.ConnectionPOJO;
import main.java.helper.DatabaseHelper;
import main.java.model.Context;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by Henri on 4.3.2016.
 *
 * Handles user inputs in main window
 */
public class MainController implements Initializable {
    @FXML
    private TextArea log;
    @FXML
    private TextField databaseUrl, databaseUser, databasePassword;
    @FXML
    private TabPane resultsTabPane;
    @FXML
    private AnchorPane metadataPane;
    @FXML
    public ComboBox connectionsComboBox;
    @FXML
    public SplitPane mainArea;

    public DatabaseHelper databaseHelper;
    public CodeArea queryArea;

    /**
     * initializes queryArea and makes bindings
     * RichTextFX doesn't support fxml so we need to initialize those components in code
     * @param url not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // make binding
        connectionsComboBox.itemsProperty().bind(Context.getInstance().getConnections());

        // init queryArea
        queryArea = new CodeArea();
        queryArea.setParagraphStyle(0, Collections.singletonList("has-caret"));
        queryArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                runQuery(new ActionEvent());
            }
        });

        VirtualizedScrollPane sp = new VirtualizedScrollPane(queryArea);
        mainArea.getItems().add(0, sp);
    }

    /**
     * executes a sql query
     * @param event not used
     */
    @FXML
    protected void runQuery(ActionEvent event) {
        try {
            String query = (!queryArea.getSelectedText().equals("")) ? queryArea.getSelectedText() : queryArea.getText(queryArea.getCurrentParagraph());
            ResultSet resultSet = null;
            int affectedRows = 0;
            String queryType = query.split(" ")[0];
            if (queryType.equalsIgnoreCase("select")) {
                resultSet = databaseHelper.executeQuery(query);
            // is this check really needed?
            } else if (queryType.equalsIgnoreCase("insert") || queryType.equalsIgnoreCase("update") || queryType.equalsIgnoreCase("delete") || queryType.equalsIgnoreCase("alter")){
                affectedRows = databaseHelper.executeUpdate(query);
            } else {
                writeLog("Query type %s is not supported. Use select, insert, update or delete.", queryType);
                return;
            }
            if (resultSet != null) {
                buildTableData(resultSet);
            } else {
                writeLog("Query completed successfully. %s rows affected.", affectedRows);
            }
            databaseHelper.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                writeLog(e.getMessage());
            } else { writeLog("An error happened"); }
        }
    }

    /**
     * reads contents of a file to queryArea
     * @param event not used
     */
    @FXML
    protected void openSqlFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose sql file to open");
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                FileHelper fileHelper = new FileHelper();
                String content = fileHelper.readTextFromFile(file);
                queryArea.appendText(content);
                writeLog("Successfully opened file");
            } catch (IOException e) {
                writeLog("Opening file failed, error message: %s", e.getMessage());
            }
        }
    }

    /**
     * writes contents of the queryArea to file
     * @param event not used
     */
    @FXML
    protected void saveSqlSheet(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file where to save");
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                FileHelper fileHelper = new FileHelper();
                fileHelper.writeTextToFile(file, queryArea.getText());
                writeLog("Save completed");
            } catch (IOException e){
                writeLog("Saving failed, error message: %s", e.getMessage());
            }
        }
    }

    /**
     * runs selected sql query and writes results to a file in comma separated format
     * @param event not used
     */
    @FXML
    protected void exportResultsToFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file where to save");
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                String query = queryArea.getText();
                ResultSet resultSet = null;
                String queryType = query.split(" ")[0];
                if (queryType.equalsIgnoreCase("select")) {
                    resultSet = databaseHelper.executeQuery(query);
                } else {
                    writeLog("Use select clause to get data for saving");
                    return;
                }
                StringBuilder results = new StringBuilder();
                int columns = resultSet.getMetaData().getColumnCount();

                while (resultSet.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i < columns; i++) {
                        row.append(resultSet.getString(i));
                        row.append(',');
                    }
                    row.append(resultSet.getString(columns));
                    results.append(row.toString());
                    results.append(System.lineSeparator());
                }

                databaseHelper.getStatement().close();

                FileHelper fileHelper = new FileHelper();
                fileHelper.writeTextToFile(file, results.toString());
                writeLog("Save completed");
            } catch (IOException|SQLException e){
                writeLog("Saving failed, error message: %s", e.getMessage());
            }
        }
    }

    /**
     * establishes a connection to database
     * @param event not used
     */
    @FXML
    protected void connect(ActionEvent event) {
        String url = databaseUrl.getText();
        String user = databaseUser.getText();
        String password = databasePassword.getText();

        if(!url.isEmpty() && !user.isEmpty() && !password.isEmpty()) {
            try {
                databaseHelper = new DatabaseHelper(url, user, password);
                writeLog("Successfully connected to " + url);
                buildDatabaseMetaData();
            } catch (Exception e) {
                e.printStackTrace();
                writeLog(e.getMessage());
            }
        } else {
            writeLog("Please fill all fields");
        }
    }

    /**
     * opens a new window containing controls for managing connections
     * @param event not used
     */
    @FXML
    protected void openAliasWindow(ActionEvent event) {
        try {
            Parent window = FXMLLoader.load(getClass().getResource("/main/resources/alias_window.fxml"));
            Scene scene = new Scene(window, 400, 200);
            Stage windowStage = new Stage();

            windowStage.initModality(Modality.APPLICATION_MODAL);
            windowStage.setScene(scene);
            windowStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adds connection's information to corresponding fields when connection is selected from dropdown box
     * @param event not used
     */
    @FXML
    protected void selectDatabaseConnection(ActionEvent event) {
        ConnectionPOJO connectionPOJO = (ConnectionPOJO) connectionsComboBox.getSelectionModel().getSelectedItem();
        databaseUrl.setText(connectionPOJO.getDatabaseUrl());
        databaseUser.setText(connectionPOJO.getUsername());
        databasePassword.setText(connectionPOJO.getPassword());
    }

    /**
     * builds a tree view of database's tables upon connection
     */
    private void buildDatabaseMetaData() {
        try {
            DatabaseMetaData databaseMetaData = databaseHelper.getConnection().getMetaData();
            ResultSet tables = databaseMetaData.getTables(null,null,null,null);
            VBox vBox = new VBox();
            AnchorPane.setTopAnchor(vBox,0.0);
            AnchorPane.setBottomAnchor(vBox,0.0);
            AnchorPane.setLeftAnchor(vBox,0.0);
            AnchorPane.setRightAnchor(vBox,0.0);
            while (tables.next()) {
                String tableName = tables.getString(3);
                TreeItem<String> table = new TreeItem<>(tableName);

                ResultSet columns = databaseMetaData.getColumns(null,null,tableName,null);
                while(columns.next()){
                    String columnData = columns.getString(4) + " - " + JDBCType.valueOf(columns.getInt(5)).getName();
                    TreeItem<String> column = new TreeItem<>(columnData);
                    table.getChildren().add(column);
                }

                TreeView<String> treeView = new TreeView<>(table);
                vBox.getChildren().add(treeView);
            }
            metadataPane.getChildren().clear();
            metadataPane.getChildren().add(vBox);
        } catch (SQLException e){
            e.printStackTrace();
            writeLog(e.getMessage());
        }
    }

    /**
     * builds a table view of a sql result set and adds it to tab panel
     * @param resultSet sql result set
     */
    private void buildTableData(ResultSet resultSet) {
        ObservableList<ObservableList> observableList = FXCollections.observableArrayList();
        TableView tableView = new TableView();
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn tableColumn = new TableColumn(resultSetMetaData.getColumnName(i + 1));
                tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> p) {
                        return new ReadOnlyObjectWrapper(p.getValue().get(j).toString());
                    }
                });

                tableView.getColumns().add(tableColumn);
            }

            // add data to columns
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                observableList.add(row);
            }

            tableView.setItems(observableList);

            // add table to new tab and select it
            Tab tab = new Tab("Resultset " + (resultsTabPane.getTabs().size() + 1));
            tab.setContent(tableView);
            resultsTabPane.getTabs().add(tab);
            resultsTabPane.getSelectionModel().select(tab);

            String resultText = String.format("Query returned %s results.", observableList.size());
            writeLog(resultText);
        } catch (SQLException e) {
            e.printStackTrace();
            writeLog("Failed to build table data");
        }
    }

    /**
     * appends a message to log area
     * @param message message to append
     */
    private void writeLog(String message) {
        log.appendText(message + "\n");
    }

    /**
     * appends a formatted message to log area
     * @param formattedMessage formatted message to append
     * @param params parameters for formatted message
     */
    private void writeLog(String formattedMessage, Object... params) {
        log.appendText(String.format(formattedMessage, params) + "\n");
    }

}
