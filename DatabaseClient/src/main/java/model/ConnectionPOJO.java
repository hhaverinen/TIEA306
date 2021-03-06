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

package model;

/**
 * Created by Henri on 18.3.2016.
 *
 * Model for connection
 */
public class ConnectionPOJO implements PojoInterface {

    private String name;
    private DriverPOJO driver;
    private String username;
    private String password;
    private String databaseUrl;

    public ConnectionPOJO(String name, DriverPOJO driver, String username, String password, String databaseUrl) {
        this.name = name;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.databaseUrl = databaseUrl;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public DriverPOJO getDriver() {
        return driver;
    }

    public void setDriver(DriverPOJO driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

}
