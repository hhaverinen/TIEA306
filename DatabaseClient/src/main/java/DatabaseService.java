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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henri on 4.3.2016.
 */
public class DatabaseService {

    public static List<Map> executeQuery(String query){

        Connection con = null;
        Statement statement = null;
        List<Map> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://192.168.1.254/mock_base", "dbuser", "passw0rd");
            statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Map<String,String> map = new HashMap<>();
                map.put("id",resultSet.getInt("id")+"");
                map.put("first_name",resultSet.getString("first_name"));
                map.put("last_name",resultSet.getString("last_name"));

                results.add(map);
            }
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
        return results;
    }
}
