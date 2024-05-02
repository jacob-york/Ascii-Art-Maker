package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.york.asciiArtMaker.AppUtil.JFXColorToJavaColor;

public class ImageRenderer {

    // todo: getters w/ no setters. Practically a record.
    private static double ADJ_CONSTANT = 1.5;
    private double fontPoint;
    private String font;
    private int width;
    private int height;
    private Color bgColor;
    private Color textColor;


    public ImageRenderer(double fontPoint, String font, int width, int height, Color bgColor, Color textColor) {
        this.fontPoint = fontPoint;
        this.font = font;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    public double getFontPoint() {
        return fontPoint;
    }

    public String getFont() {
        return font;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public BufferedImage render(String content, int imgType) {
        BufferedImage image = new BufferedImage(width, height, imgType);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(JFXColorToJavaColor(bgColor));
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2.setPaint(JFXColorToJavaColor(textColor));
        g2.setFont(new Font(AsciiArtPane.getDefaultFont(), Font.PLAIN, (int) Math.round(fontPoint)));
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            g2.drawString(lines[i], 0, (g2.getFontMetrics().getHeight() * (i+1)));
        }

        g2.dispose();
        return image;
    }
}
