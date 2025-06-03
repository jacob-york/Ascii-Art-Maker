package com.york.asciiArtMaker.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class HighlightableText extends TextFlow {

    private class TextWithBackground extends StackPane {
        private Text text;
        private Rectangle rectangle;

        public TextWithBackground(String str) {
            super();
            setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            text = new Text(str);
            rectangle = new Rectangle();
            rectangle.setFill(Color.rgb(5, 55, 158));
            rectangle.setWidth(text.getBoundsInParent().getWidth());
            rectangle.setHeight(text.getBoundsInParent().getHeight());
            getChildren().addAll(rectangle, text);
        }

        public Font getFont() {
            return text.getFont();
        }
        public void setFont(Font font) {
            text.setFont(font);
        }

        public void setFill(Color color) {
            text.setFill(color);
        }

        public void setHighlight(Color color) {
            rectangle.setFill(color);
        }

        public void setText(String string) {
            text.setText(string);
        }
    }

    private String content;
    private Font font;
    private final Text before;
    private final Text highlighted;
    private final Text after;

    public HighlightableText() {
        super();
        content = null;
        font = new Font(AsciiViewportPane.getDefaultFontName(), 13);

        before = new Text("");
        before.setFont(font);

        highlighted = new Text("");
        highlighted.setFont(font);
        highlighted.setFill(Color.WHITE);

        after = new Text("");
        after.setFont(font);

        setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        getChildren().addAll(before, highlighted, after);
    }

    public Font getFont() {
        return font;
    }
    public void setFont(Font newFont) {
        font = newFont;
        before.setFont(font);
        highlighted.setFont(font);
        after.setFont(font);
    }

    public void setFill(Color newTextColor) {
        before.setFill(newTextColor);
        after.setFill(newTextColor);
    }

    public String getText() {
        return String.copyValueOf(content.toCharArray());
    }

    public void setText(String newContent) {
        content = String.copyValueOf(newContent.toCharArray());
        int oneThird = content.length() / 3 - 10;
        before.setText(content.substring(0, oneThird));
        highlighted.setText(content.substring(oneThird + 1, 2*oneThird));
        after.setText(content.substring(2*oneThird + 1));
    }
}
