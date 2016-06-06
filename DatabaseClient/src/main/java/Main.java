
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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.controller.MainController;
import main.java.model.ConnectionPOJO;
import main.java.model.Context;

/**
 * Created by Henri on 4.3.2016.
 */
public class Main extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/main/resources/base.fxml").openStream());
        mainController = loader.getController();
        primaryStage.setTitle("DatabaseClient");
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add("/main/resources/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // for now, ugly(?) way to close connection...
        if(mainController.databaseHelper != null)
            if(mainController.databaseHelper.getConnection() != null)
                mainController.databaseHelper.getConnection().close();

    }

    public static void main(String[] args) { launch(args); }
}
