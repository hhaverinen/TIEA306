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
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by Henri on 4.3.2016.
 */
public class Controller {
    @FXML
    private TextArea queryArea;
    @FXML
    private TextArea log;
    @FXML
    TabPane resultsTabPane;

    @FXML
    protected void runQuery(ActionEvent event) {
        try {
            String query = queryArea.getText();

            DatabaseHelper databaseHelper = new DatabaseHelper("jdbc:mysql://192.168.1.254/mock_base", "dbuser", "passw0rd");
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
                log.setText(String.format("Query completed succesfully. %s rows affected.", affectedRows));
            }
            databaseHelper.getConnection().close();
            databaseHelper.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
            log.setText(e.getMessage());
        }
    }

    @FXML
    protected void clearQuery(ActionEvent event) {
        queryArea.clear();
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
