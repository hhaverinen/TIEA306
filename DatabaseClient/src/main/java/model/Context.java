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

package main.java.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.helper.FileHelper;

import java.util.List;

/**
 * Created by Henri on 18.3.2016.
 */
public class Context {

    private final static Context instance = new Context();
    private ObjectProperty<ObservableList<ConnectionPOJO>> connections = new SimpleObjectProperty<>();

    private Context() {
        connections.set(FXCollections.observableArrayList());

        FileHelper helper = new FileHelper();
        List<ConnectionPOJO> pojos = helper.readConnectionFromFile("aliases.json");
        if (pojos != null) {
            connections.get().addAll(pojos);
        }
    }

    public static Context getInstance(){
        return instance;
    }

    public ObjectProperty<ObservableList<ConnectionPOJO>> getConnections(){
        return connections;
    }
}
