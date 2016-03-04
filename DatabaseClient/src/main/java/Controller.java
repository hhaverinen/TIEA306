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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henri on 4.3.2016.
 */
public class Controller {
    @FXML private TextArea queryArea;
    @FXML private TextArea resultArea;

    @FXML protected void runQuery(ActionEvent event) {
        resultArea.clear();

        try {
            List<Map> results = DatabaseService.executeQuery(queryArea.getText());
            StringBuilder sb = new StringBuilder("ID,Etunimi,Sukunimi\n");
            for (Map map : results) {
                sb.append(map.get("id") + ", " + map.get("first_name") + ", " + map.get("last_name")+"\n");
            }
            resultArea.setText(sb.toString());
        } catch (Exception e) {

        }
    }

    @FXML protected void clearQuery(ActionEvent event) {
        queryArea.clear();
    }

}
