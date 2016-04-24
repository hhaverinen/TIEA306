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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.model.ConnectionPOJO;
import main.java.model.DriverPOJO;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Henri on 22.3.2016.
 */
public class FileHelper {

    public void writeObjectsToJsonFile(List objects, String fileName) {
        File file = new File(fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {

            if (!file.exists()) {
                file.createNewFile();
            }
            Gson gson = new Gson();
            bufferedWriter.write(gson.toJson(objects));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List readPojosFromJsonFile(String filename, Type collectionType) {
        String content = null;
        try {
            content = readTextFromFile(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        return gson.fromJson(content, collectionType);
    }

    public void writeTextToFile(File file, String text) throws IOException {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {

            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedWriter.write(text);
        }
    }

    public String readTextFromFile(File file) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            StringBuilder content = new StringBuilder();
            int character;
            while ((character =  bufferedReader.read()) != -1) {
                content.append((char) character);
            }

            return content.toString();
        }
    }

}
