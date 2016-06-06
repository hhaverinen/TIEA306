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

package main.java.component;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import main.java.model.PojoInterface;

/**
 * Created by Henri on 24.4.2016.
 *
 * Custom ComboBox for rendering data of pojos
 */
public class PojoComboBox extends ComboBox {

    public PojoComboBox() {
        super();
        this.initLookAndFeel();
    }

    /**
     * sets behaviour of ComboBox, shows pojo's name in list and selection
     */
    private void initLookAndFeel() {
        this.setCellFactory(new Callback<ListView<PojoInterface>, ListCell<PojoInterface>>() {
            @Override
            public ListCell<PojoInterface> call(ListView<PojoInterface> p) {
                ListCell cell = new ListCell<PojoInterface>() {
                    @Override
                    protected void updateItem(PojoInterface item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
                return cell;
            }
        });

        this.setButtonCell(new ListCell<PojoInterface>() {
            @Override
            protected void updateItem(PojoInterface item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }

            }
        });
    }
}
