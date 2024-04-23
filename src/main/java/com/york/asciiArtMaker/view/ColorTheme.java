package com.york.asciiArtMaker.view;

import javafx.scene.paint.Color;

    public enum ColorTheme {

        NOTEPAD("Notepad", Color.WHITE, Color.BLACK, false),
        COMMAND_PROMPT("Command Prompt", Color.BLACK, Color.rgb(192, 192, 192), true),
        POWERSHELL("PowerShell", Color.rgb(1, 36, 86), Color.WHITE, true),
        GREEN_MONOCHROME("Green Monochrome", Color.BLACK, Color.rgb(102, 255, 102), true),
        AMBER_MONOCHROME("Amber Monochrome", Color.BLACK, Color.rgb(255, 176, 0), true),
        JETBRAINS("JetBrains", Color.rgb(43, 43, 43), Color.rgb(169, 183, 198), true);

        private final String name;

        private final Color bgColor;

        private final Color textColor;

    private final boolean invertedShading;

    ColorTheme(String name, Color bgColor, Color textColor, boolean invertedShading) {
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
