package com.york.asciiArtMaker.view;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class ThemeMenuItem extends MenuItem {

    public static final Font FONT = new Font("Consolas", 20.0);
    public final ColorTheme colorTheme;

    public ThemeMenuItem(ColorTheme colorTheme, Runnable runnable) {
        this.colorTheme = colorTheme;

        Label nameLabel = new Label(colorTheme.name);
        nameLabel.setFont(FONT);
        nameLabel.setTextFill(colorTheme.textColor);
        nameLabel.setStyle("-fx-background-color: #" + colorToHexCode(colorTheme.bgColor) + ";");

        this.setGraphic(nameLabel);

        this.setOnAction((ActionEvent event) -> runnable.run());
    }

    public static String colorToHexCode(Color color) {
        return color.toString().substring(2, 8);
    }
}
