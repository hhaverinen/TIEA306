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

import helper.DatabaseHelper;

/**
 * Created by Henri on 28.6.2016.
 *
 * Object for wrapping name and DatabaseHelper object,
 * Purely for displaying purposes
 */
public class DatabaseConnection implements PojoInterface {

    String name;
    DatabaseHelper databaseHelper;

    public DatabaseConnection(String name, DatabaseHelper databaseHelper) {
        this.name = name;
        this.databaseHelper = databaseHelper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
}
