
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Henri on 4.3.2016.
 */
public class Main extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/main/resources/base.fxml").openStream());
        mainController = loader.getController();

        // for testing purpose
        ObjectProperty<ObservableList> testi = new SimpleObjectProperty<>();
        testi.set(FXCollections.observableArrayList(new ConnectionPOJO("jep","jop","pass","url")));
        mainController.connectionsComboBox.itemsProperty().bind(testi);

        primaryStage.setTitle("DatabaseClient");
        primaryStage.setScene(new Scene(root, 800, 500));
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
