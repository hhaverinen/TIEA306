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

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.sql.*;
import java.util.List;

/**
 * Created by Henri on 4.3.2016.
 */
public class Controller {
    @FXML
    private TextArea queryArea, log;
    @FXML
    private TextField databaseUrl, databaseUser, databasePassword;
    @FXML
    private TabPane resultsTabPane;
    @FXML
    private AnchorPane metadataPane;

    protected DatabaseHelper databaseHelper;

    @FXML
    protected void queryAreaOnKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
            runQuery(new ActionEvent());
        }
    }

    @FXML
    protected void runQuery(ActionEvent event) {
        try {
            String query = queryArea.getText();
            ResultSet resultSet = null;
            int affectedRows = 0;
            if (query.split(" ")[0].equalsIgnoreCase("select")) {
                resultSet = databaseHelper.executeQuery(query);
            } else {
                affectedRows = databaseHelper.executeUpdate(query);
            }
            if (resultSet != null) {
                buildTableData(resultSet);
            } else {
                log.setText(String.format("Query completed successfully. %s rows affected.", affectedRows));
            }
            databaseHelper.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
            log.setText(e.getMessage());
        }
    }


    @FXML
    protected void connect(ActionEvent event) {
        String url = databaseUrl.getText();
        String user = databaseUser.getText();
        String password = databasePassword.getText();

        try {
            databaseHelper = new DatabaseHelper(url, user, password);
            log.setText("Successfully connected to " + url);
            buildDatabaseMetaData();
        } catch (Exception e) {
            e.printStackTrace();
            log.setText(e.getMessage());
        }
    }

    private void buildDatabaseMetaData() {
        try {
            DatabaseMetaData databaseMetaData = databaseHelper.getConnection().getMetaData();
            ResultSet tables = databaseMetaData.getTables(null,null,null,null);
            VBox vBox = new VBox();
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
            metadataPane.getChildren().add(vBox);
        } catch (SQLException e){
            e.printStackTrace();
            log.setText(e.getMessage());
        }
    }

    private void buildTableData(ResultSet resultSet) {
        ObservableList<ObservableList> observableList = FXCollections.observableArrayList();
        TableView tableView = new TableView();
        try {
            // clear old columns and add new ones
            tableView.getColumns().clear();
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
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    row.add(resultSet.getString(i + 1));
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
            log.setText(resultText);
        } catch (SQLException e) {
            e.printStackTrace();
            log.setText("Failed to build table data");
        }
    }

}
