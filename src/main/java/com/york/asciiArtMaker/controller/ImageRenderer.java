package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.york.asciiArtMaker.AppUtil.JFXColorToJavaColor;

public class ImageRenderer {

    // todo:
    //  adjustable resolution
    //  try and refactor this into a proper builder
    private static double ADJ_CONSTANT = 1.5;
    private double fontPoint;
    private String font;
    private int widthPixels;
    private int heightPixels;
    private Color bgColor;
    private Color textColor;
    private int imageType;

    public ImageRenderer(double fontPoint, int widthPixels, int heightPixels, String font) {
        this.fontPoint = fontPoint;
        this.widthPixels = widthPixels;
        this.heightPixels = heightPixels;
        this.bgColor = Color.WHITE;
        this.textColor = Color.BLACK;
        this.font = font;

        imageType = BufferedImage.TYPE_3BYTE_BGR;
    }

    public int getImageType() {
        return imageType;
    }
    public ImageRenderer setImageType(int imageType) {
        this.imageType = imageType;
        return this;
    }
    public String getFont() {
        return font;
    }


    public Color getBgColor() {
        return bgColor;
    }
    public ImageRenderer setBgColor(Color newBgColor) {
        bgColor = newBgColor;
        return this;
    }

    public Color getTextColor() {
        return textColor;
    }
    public ImageRenderer setTextColor(Color newTextColor) {
        textColor = newTextColor;
        return this;
    }

    public double getFontPoint() {
        return fontPoint;
    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public int getHeightPixels() {
        return heightPixels;
    }

    public BufferedImage render(AsciiImage art) {
        return render(art, imageType);
    }
    public BufferedImage render(AsciiImage art, int imgType) {
        BufferedImage image = new BufferedImage(widthPixels, heightPixels, imgType);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(JFXColorToJavaColor(bgColor));
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2.setPaint(JFXColorToJavaColor(textColor));
        g2.setFont(new Font(AsciiArtPane.getDefaultFont(), Font.PLAIN, (int) Math.round(fontPoint)));
        String[] lines = art.toStr().split("\n");

        for (int i = 0; i < lines.length; i++) {
            g2.drawString(lines[i], 0, (g2.getFontMetrics().getHeight() * (i+1)));
        }

        g2.dispose();
        return image;
    }
}
