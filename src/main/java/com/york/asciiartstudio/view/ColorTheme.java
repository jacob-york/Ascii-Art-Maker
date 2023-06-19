package com.york.asciiartstudio.view;

import javafx.scene.paint.Color;

public class ColorTheme {

    private final String name;

    private final Color bgColor;

    private final Color textColor;

    private final boolean invertedShading;


    public ColorTheme(String name, Color bgColor, Color textColor, boolean invertedShading) {
        this.name = name;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.invertedShading = invertedShading;
    }

    public Color getBGColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public boolean getInvertedShading() {
        return invertedShading;
    }

    public String getName() {
        return name;
    }

}
