package com.york.asciiArtMaker.view;

import javafx.scene.paint.Color;

public enum ColorTheme {

    NOTEPAD("Notepad", Color.WHITE, Color.BLACK, false),
    COMMAND_PROMPT("Command Prompt", Color.BLACK, Color.rgb(192, 192, 192), true),
    POWERSHELL("PowerShell", Color.rgb(1, 36, 86), Color.WHITE, true),
    GREEN_MONOCHROME("Green Mono", Color.BLACK, Color.rgb(102, 255, 102), true),
    AMBER_MONOCHROME("Amber Mono", Color.BLACK, Color.rgb(255, 176, 0), true),
    JETBRAINS("JetBrains", Color.rgb(43, 43, 43), Color.rgb(169, 183, 198), true);

    public final String name;
    public final Color bgColor;
    public final Color textColor;

    public final boolean invertedShading;

    ColorTheme(String name, Color bgColor, Color textColor, boolean invertedShading) {
        this.name = name;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.invertedShading = invertedShading;
    }
}
