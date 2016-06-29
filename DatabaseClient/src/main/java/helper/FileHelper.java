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

package helper;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Henri on 22.3.2016.
 *
 * Helper class for writing and reading stuff to/from files
 */
public class FileHelper {

    /**
     * writes pojos to file as json
     * @param objects list of pojo objects to write to file
     * @param fileName name of the file where to write
     */
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

    /**
     * reads pojos from a json file to a list
     * @param filename name of the file from where to read
     * @param collectionType type of objects that json file contains
     * @return returns list of a objects read from the file
     */
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

    /**
     * writes text to a file
     * @param file file to write
     * @param text text to write
     * @throws IOException
     */
    public void writeTextToFile(File file, String text) throws IOException {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {

            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedWriter.write(text);
        }
    }

    /**
     * reads text from the file
     * @param file file where to read
     * @return returns contents of the file as a string
     * @throws IOException
     */
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
