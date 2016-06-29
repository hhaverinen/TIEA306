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

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.component.PojoComboBox;
import main.java.helper.FileHelper;
import main.java.model.ConnectionPOJO;
import main.java.helper.DatabaseHelper;
import main.java.model.Context;
import main.java.model.DatabaseConnection;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Henri on 4.3.2016.
 *
 * Handles user inputs in main window
 * Also does some drawing
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
    private PojoComboBox connectionsComboBox, activeConnectionsComboBox;
    @FXML
    private ComboBox<String> queryHistoryComboBox;
    @FXML
    private SplitPane mainArea;

    private DatabaseHelper currentDatabaseConnection;
    private CodeArea queryArea;

    private static String COMMENTMARK = "--";

    /**
     * initializes queryArea and makes bindings and event handlers
     * RichTextFX doesn't support fxml so we need to initialize those components in code
     * @param url not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // make bindings
        connectionsComboBox.itemsProperty().bind(Context.getInstance().getConnections());
        activeConnectionsComboBox.itemsProperty().bind(Context.getInstance().getActiveConnections());

        // save reference of selected active connection
        activeConnectionsComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            DatabaseConnection databaseConnection = (DatabaseConnection) activeConnectionsComboBox.getSelectionModel().getSelectedItem();
            currentDatabaseConnection = databaseConnection.getDatabaseHelper();

        });

        // append query to queryArea when query is selected from queryHistory comboBox
        queryHistoryComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            queryArea.appendText("\n\n" + newValue);
        });

        // init queryArea
        queryArea = new CodeArea();

        // CTRL + ENTER shortcut for running query
        queryArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                invokeRunQuery(new ActionEvent());
            }
        });

        // text change listener for adding styles to comments in queryArea
        queryArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            queryArea.setStyleSpans(0, setQueryAreaCommentStyles(queryArea.getText()));
        });

        // add scrollbars to queryArea
        VirtualizedScrollPane sp = new VirtualizedScrollPane(queryArea);
        mainArea.getItems().add(0, sp);

        // set focus on queryArea
        Platform.runLater(() -> queryArea.requestFocus());
    }

    /**
     * sets styles to comments in query area
     * a lot of reference was taken from RichTextFX demos, see
     * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywords.java
     * @return returns StyleSpans including styles for queryarea
     */
    private StyleSpans<Collection<String>> setQueryAreaCommentStyles(String text) {
        Matcher commentMatcher = Pattern.compile(COMMENTMARK + "[^\n]*").matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastCommentEnd = 0;
        while(commentMatcher.find()) {
            // "moves cursor" where to start adding new styles
            spansBuilder.add(Collections.emptyList(), commentMatcher.start() - lastCommentEnd);
            spansBuilder.add(Collections.singleton("comment"), commentMatcher.end() - commentMatcher.start());
            lastCommentEnd = commentMatcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastCommentEnd);
        return spansBuilder.create();
    }

    /**
     * closes the application
     * @param event not used
     */
    @FXML
    protected void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    /**
     * gets sql query from queryArea and executes it
     * @param event
     */
    @FXML
    protected void invokeRunQuery(ActionEvent event) {
        String query = (!queryArea.getSelectedText().equals("")) ? queryArea.getSelectedText() : queryArea.getText(queryArea.getCurrentParagraph());
        query = removeCommentsFromQuery(query);
        runQuery(query);
    }

    /**
     * removes comments from query text
     * @param query query text where to remove comments
     * @return returns text where comments has been removed
     */
    private String removeCommentsFromQuery(String query) {
        return query.split(COMMENTMARK)[0];
    }

    /**
     * executes a sql query
     * @param query sql query to be executed
     */
    protected void runQuery(String query) {
        ResultSet resultSet = null;
        try {
            int affectedRows = 0;
            String queryType = query.split(" ")[0];
            if (queryType.equalsIgnoreCase("select")) {
                resultSet = currentDatabaseConnection.executeQuery(query);
            // is this check really needed?
            } else if (queryType.equalsIgnoreCase("insert") || queryType.equalsIgnoreCase("update") || queryType.equalsIgnoreCase("delete") || queryType.equalsIgnoreCase("alter")){
                affectedRows = currentDatabaseConnection.executeUpdate(query);
            } else {
                writeLog("Query type %s is not supported. Use select, insert, update or delete.", queryType);
                return;
            }
            if (resultSet != null) {
                buildTableData(resultSet);
            } else {
                writeLog("Query completed successfully. %s rows affected.", affectedRows);
            }
            queryHistoryComboBox.getItems().add(query);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                writeLog(e.getMessage());
            } else { writeLog("An error happened"); }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            currentDatabaseConnection.closeStatement();

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL file", "*.sql"));
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL file", "*.sql"));
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
        fileChooser.setTitle("Choose file where to save");
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        ResultSet resultSet = null;
        if (file != null) {
            try {
                String query = queryArea.getText();
                String queryType = query.split(" ")[0];
                if (queryType.equalsIgnoreCase("select")) {
                    resultSet = currentDatabaseConnection.executeQuery(query);
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

                FileHelper fileHelper = new FileHelper();
                fileHelper.writeTextToFile(file, results.toString());
                writeLog("Save completed");
            } catch (IOException|SQLException e){
                writeLog("Saving failed, error message: %s", e.getMessage());
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                currentDatabaseConnection.closeStatement();
            }
        }
    }

    /**
     * executes sql query straight from file
     * @param event not used
     */
    @FXML
    protected void runSqlFromFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose sql file to execute");
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                FileHelper fileHelper = new FileHelper();
                String query = fileHelper.readTextFromFile(file);
                writeLog("Read completed");
                runQuery(query);
            } catch (IOException e){
                writeLog("Reading failed, error message: %s", e.getMessage());
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
                DatabaseHelper databaseHelper = new DatabaseHelper(url, user, password);
                // use url as name of the DatabaseConnection object
                DatabaseConnection databaseConnection = new DatabaseConnection(url, databaseHelper);
                activeConnectionsComboBox.getItems().add(databaseConnection);
                activeConnectionsComboBox.getSelectionModel().select(databaseConnection);
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
            scene.getStylesheets().add("/main/resources/styles.css");
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
            DatabaseMetaData databaseMetaData = currentDatabaseConnection.getDatabaseMetaData();
            ResultSet tables = databaseMetaData.getTables(null,null,null,null);
            VBox vBox = new VBox();
            AnchorPane.setTopAnchor(vBox,0.0);
            AnchorPane.setBottomAnchor(vBox,0.0);
            AnchorPane.setLeftAnchor(vBox,0.0);
            AnchorPane.setRightAnchor(vBox,0.0);
            TreeItem<String> rootNode = new TreeItem<>("Tables"); //TODO: get db name or something?
            while (tables.next()) {
                String tableName = tables.getString(3);
                TreeItem<String> table = new TreeItem<>(tableName);

                ResultSet columns = databaseMetaData.getColumns(null,null,tableName,null);
                while(columns.next()){
                    String columnData = columns.getString(4) + " - " + JDBCType.valueOf(columns.getInt(5)).getName() + " (" + columns.getString(7) + ")";
                    TreeItem<String> column = new TreeItem<>(columnData);
                    table.getChildren().add(column);
                }

                rootNode.getChildren().add(table);
            }
            TreeView<String> treeView = new TreeView<>(rootNode);
            vBox.getChildren().add(treeView);
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
        tableView.getSelectionModel().setCellSelectionEnabled(true);
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

            // add CTRL + C listener
            tableView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.C && event.isControlDown()) {
                    //get position of selected cell
                    TablePosition tablePosition = tableView.getFocusModel().getFocusedCell();
                    // get data of selected row
                    List<String> selectedRow = (List)tableView.getSelectionModel().getSelectedItem();
                    // get data of selected cell
                    String cellText = selectedRow.get(tablePosition.getColumn()).toString();
                    // store data to clipboard
                    ClipboardContent content = new ClipboardContent();
                    content.putString(cellText);
                    Clipboard.getSystemClipboard().setContent(content);
                }
            });

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
