
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

package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.MainController;
import model.Context;


/**
 * Created by Henri on 4.3.2016.
 *
 * Main class, sets resources and starts the application
 */
public class Main extends Application {

    private MainController mainController;

    /**
     * starts the application
     * @param primaryStage primary stage of the application
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/base.fxml").openStream());
        mainController = loader.getController();
        primaryStage.setTitle("DatabaseClient");
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * stops application, also makes sure that database connection is closed
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        // close active database connections
        Context.getInstance().getActiveConnections().get().stream().forEach(databaseConnection -> databaseConnection.getDatabaseHelper().closeConnection());
    }

    public static void main(String[] args) { launch(args); }
}
