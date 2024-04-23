package com.york.asciiArtMaker.view;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class ThemeMenuItem extends MenuItem {

    private static final Font FONT = new Font("Consolas", 20.0);
    private final ColorTheme colorTheme;

    public ThemeMenuItem(ColorTheme colorTheme, Runnable runnable) {
        this.colorTheme = colorTheme;

        Label nameLabel = new Label(colorTheme.getName());
        nameLabel.setFont(FONT);
        nameLabel.setTextFill(colorTheme.getTextColor());
        nameLabel.setStyle("-fx-background-color: #" + colorToHexCode(colorTheme.getBGColor()) + ";");

        this.setGraphic(nameLabel);

        this.setOnAction((ActionEvent event) -> runnable.run());
    }

    public static String colorToHexCode(Color color) {
        return color.toString().substring(2, 8);
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }
}
