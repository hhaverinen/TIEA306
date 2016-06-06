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

package main.java.helper;

import java.sql.*;

/**
 * Created by Henri on 4.3.2016.
 *
 * Helper for managing database connection
 */
public class DatabaseHelper {

    private Connection connection;
    private Statement statement;

    private final int connectionTimeout = 10000;
    private final int socketTimeout = 10000;

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement(){
        return statement;
    }

    /**
     * constructor for DatabaseHelper
     * @param url database's url
     * @param user database's user
     * @param password user's password
     * @throws Exception
     */
    public DatabaseHelper(String url, String user, String password) throws Exception {
        //Class.forName("com.mysql.jdbc.Driver").newInstance(); // not needed?
        connection = DriverManager.getConnection(url + "?" + getTimeoutParams(connectionTimeout, socketTimeout), user, password);
    }

    /**
     * executes select queries to database
     * @param query query to execute
     * @return returns resultset of query
     * @throws SQLException
     */
    public ResultSet executeQuery(String query) throws SQLException {
        statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    /**
     * executes insert, update or delete queries to database
     * @param query query to execute
     * @return returns number of affected rows
     * @throws SQLException
     */
    public int executeUpdate(String query) throws SQLException {
        statement = connection.createStatement();
        return statement.executeUpdate(query);
    }

    /**
     * returns timeout parameters in url format
     * @param connectionTimeout timeout for connection
     * @param socketTimeout timeout for socket
     * @return returns string containing timeout parameters in url format
     */
    private String getTimeoutParams(int connectionTimeout, int socketTimeout) {
        return "connectTimeout=" + connectionTimeout + "&socketTimeout=" + socketTimeout;
    }
    

}
