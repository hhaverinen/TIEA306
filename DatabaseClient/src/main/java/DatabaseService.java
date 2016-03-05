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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henri on 4.3.2016.
 */
public class DatabaseService {


    private DatabaseService() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(String query, TableView tableView) {

        Connection con = null;
        Statement statement = null;
        ObservableList<ObservableList> observableList = FXCollections.observableArrayList();
        try {
            con = DriverManager.getConnection("jdbc:mysql://192.168.1.254/mock_base", "dbuser", "passw0rd");
            statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // clear old columns and add new ones
            tableView.getColumns().clear();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn tableColumn = new TableColumn(resultSetMetaData.getColumnName(i+1));
                tableColumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> p) {
                        return new ReadOnlyObjectWrapper(p.getValue().get(j).toString());
                    }
                });

                tableView.getColumns().add(tableColumn);
            }

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 0; i < resultSetMetaData.getColumnCount(); i++){
                    row.add(resultSet.getString(i+1));
                }
                observableList.add(row);
            }

            tableView.setItems(observableList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            if (con != null)
                con.close();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
