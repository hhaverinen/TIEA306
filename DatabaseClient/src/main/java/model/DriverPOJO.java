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

/**
 * Created by Henri on 5.4.2016.
 *
 * Model for driver
 */
public class DriverPOJO implements PojoInterface {

    private String name;
    private String pathToJar;

    public DriverPOJO(String name, String pathToJar) {
        this.name = name;
        this.pathToJar = pathToJar;
    }

    public String getPathToJar() {
        return pathToJar;
    }

    public void setPathToJar(String pathToJar) {
        this.pathToJar = pathToJar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * custom equals method to get ComboBox selection working
     * @param obj object to compare
     * @return boolean if object matches
     */
    @Override
    public boolean equals(Object obj) {
        DriverPOJO pojo = (DriverPOJO)obj;
        if (pojo != null)
            return (pojo.getName().equals(name) && pojo.getPathToJar().equals(pathToJar)) ? true : false;
        return false;
    }
}
