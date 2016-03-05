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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
    private TableView resultArea;
    @FXML
    private TextArea log;

    @FXML
    protected void runQuery(ActionEvent event) {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper("jdbc:mysql://192.168.1.254/mock_base", "dbuser", "passw0rd");
            ResultSet resultSet = databaseHelper.executeQuery(queryArea.getText());
            buildTableData(resultSet);
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

        try {
            // clear old columns and add new ones
            resultArea.getColumns().clear();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn tableColumn = new TableColumn(resultSetMetaData.getColumnName(i + 1));
                tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> p) {
                        return new ReadOnlyObjectWrapper(p.getValue().get(j).toString());
                    }
                });

                resultArea.getColumns().add(tableColumn);
            }

            // add data to columns
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    row.add(resultSet.getString(i + 1));
                }
                observableList.add(row);
            }

            resultArea.setItems(observableList);
            String resultText = String.format("Query returned %s results.", observableList.size());
            log.setText(resultText);
        } catch (SQLException e) {
            e.printStackTrace();
            log.setText("Failed to build table data");
        }
    }

}
