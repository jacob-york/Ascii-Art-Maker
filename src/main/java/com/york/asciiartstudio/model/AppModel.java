package com.york.asciiartstudio.model;

import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.asciiArt.AsciiArtBuilder;
import com.york.asciiartstudio.model.asciiArt.AsciiImageBuilder;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class AppModel {

    private File curChooseDir;

    private File curSaveDir;

    private AsciiArtBuilder artBuilder;

    public AppModel() {
        artBuilder = null;
    }

    public static double ensureRange(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static void copyToClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    public double calcNewFontHeight(int newCharWidth, double curFontHeight) {
        final double charWidthChange = ((double) newCharWidth / (double) artBuilder.getCharWidth());
        return curFontHeight * charWidthChange;
    }

    public File getCurChooseDir() {
        return curChooseDir;
    }

    public void setCurChooseDir(File newDir) {
        curChooseDir = newDir;
    }

    public File getCurSaveDir() {
        return curSaveDir;
    }

    public boolean artBuilderIsNull() {
        return artBuilder == null;
    }

    public String queryArt() {
        return artBuilder.getResult()[0];
    }

    public void clearArtBuilder() {
        artBuilder = null;
    }

    public void setCurSaveDir(File newDir) {
        curSaveDir = newDir;
    }

    public boolean saveAsTxt(File outputDir) {
        if (outputDir == null) return false;

        try (FileOutputStream fos = new FileOutputStream(outputDir);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);)
        {
            curSaveDir = new File(outputDir.getParent());
            osw.write(artBuilder.getResult()[0]);

        } catch (IOException e) {
            throw new RuntimeException();
        }
        return true;
    }

    public boolean setImage(File selected, int charWidth, boolean invertedShading) {
        if (selected == null) return false;

        ImageSource imageSource = new ImageFileAdapter(selected);
        artBuilder = new AsciiImageBuilder(imageSource)
                .setCharWidth(charWidth)
                .setInvertedShading(invertedShading);
        curChooseDir = new File(selected.getParent());

        return true;
    }

    public String calculateFileName() {
        final String imageName = artBuilder.getName() == null ? "model.getArtBuilder()" : artBuilder.getName();
        final String fileName = imageName + "-cw" + artBuilder.getCharWidth();
        return artBuilder.getInvertedShading() ? fileName + "-inv" : fileName;
    }

    public String getMediaName() {
        assert artBuilder != null;
        return artBuilder.getName();
    }

    public int getCharWidth() {
        assert artBuilder != null;
        return artBuilder.getCharWidth();
    }

    public int setCharWidth(int newCharWidth) {
        assert artBuilder != null;

        newCharWidth = (int) ensureRange(newCharWidth, 1, artBuilder.getMaxCharWidth());
        artBuilder.setCharWidth(newCharWidth);
        return newCharWidth;
    }

    public void setInvertedShading(boolean newVal) {
        assert artBuilder != null;
        artBuilder.setInvertedShading(newVal);
    }

}
