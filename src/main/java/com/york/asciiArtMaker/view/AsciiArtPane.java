package com.york.asciiArtMaker.view;

import com.york.asciiArtMaker.asciiArt.AsciiImage;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Pane in which ascii art is displayed.
 */
public class AsciiArtPane extends ScrollPane {

    private final String font;

    private final Text text;
    private final StackPane stackPane;

    private static final double MIN_FONT_SIZE = 1;

    public AsciiArtPane() {
        super();
        this.font = getDefaultFont();
        text = new Text();
        setStyle("-fx-font-size: 30px; -fx-background: #ffffff;");
        setContent(new Group(text));
        stackPane = new StackPane();
        getChildren().add(stackPane);
    }

    public static String getDefaultFont() {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        if (os.contains("win")) {
            return "Consolas";
        } else if (os.contains("mac") || os.contains("darwin")) {
            return "SF Mono";
        } else {
            return "Ubuntu Mono Regular";
        }
    }

    public double getFontSize() {
        return text.getFont().getSize();
    }

    public boolean setFontSize(double newFontSize) {
        if (newFontSize < MIN_FONT_SIZE) return false;

        text.setFont(new Font(font, newFontSize));
        return true;
    }

    public String getCurText() {
        return text.getText();
    }

    public void setText(AsciiImage art) {
        text.setText(art.toStr());
    }

    public int getTextAreaWidth() {
        return 0;
    }

    public int getTextAreaHeight() {
        return 0;
    }

    public String getFont() {
        return font;
    }


    public void setBGColor(Color value) {
        String hexCode = value.toString().substring(2, 8);
        setStyle("-fx-font-size: 30px; -fx-background: #" + hexCode + ";");
    }

    public void setTextColor(Color value) {
        text.setFill(value);
    }

}
