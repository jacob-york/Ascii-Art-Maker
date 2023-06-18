package com.york.asciiartstudio.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Pane in which ascii art is displayed.
 */
public class AsciiImagePane extends ScrollPane {

    private final String font;

    private final Text text;

    private Color bgColor;

    private static final double MIN_FONT_SIZE = 1;

    public AsciiImagePane(String font) {
        super();
        this.font = font;
        text = new Text();
        setFontSize(8.0);
        setStyle("-fx-font-size: 30px;");
        setContent(text);
    }

    public double getFontSize() {
        return text.getFont().getSize();
    }

    public boolean setFontSize(double newFontSize) {
        if (newFontSize < MIN_FONT_SIZE) return false;
        text.setFont(new Font(font, newFontSize));
        return true;
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String newText) {
        text.setText(newText);
    }

    public Color getBGColor() {
        return bgColor;
    }

    public void setBGColor(Color value) {
        bgColor = value;
        String hexCode = value.toString().substring(2, 8);
        setStyle("-fx-font-size: 30px; -fx-background: #" + hexCode + ";");
    }

    public Color getTextColor() {
        return (Color) text.getFill();
    }

    public void setTextColor(Color value) {
        text.setFill(value);
    }

}
