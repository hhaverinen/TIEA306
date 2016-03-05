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

import javax.xml.transform.Result;
import java.sql.*;

/**
 * Created by Henri on 4.3.2016.
 */
public class DatabaseHelper {

    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement(){
        return statement;
    }

    public DatabaseHelper(String url, String user, String password) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(url, user, password);

    }

    public ResultSet executeQuery(String query) throws SQLException {
        statement = connection.createStatement();
        return statement.executeQuery(query);

    }
}