package com.york.asciiartstudio.model.appState;

import com.york.asciiartstudio.model.asciiArt.AsciiArtBuilder;
import com.york.asciiartstudio.model.asciiArt.AsciiImageBuilder;
import com.york.asciiartstudio.view.ColorTheme;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ImageFileState {
/*
    private AsciiImageBuilder asciiImageBuilder;

    public ImageFileState() {

    }

    private boolean setAppFontHeight(double newFontHeight) {
        if (!asciiArtPane.setFontSize(newFontHeight)) return false;

        fontField.setText(String.valueOf(newFontHeight));
        return true;
    }

    private boolean setAppCharWidth(int newCharWidth) {
        assert asciiArtBuilder != null;
        if (newCharWidth <= 0) return false;
        if (newCharWidth > asciiArtBuilder.maxCharWidth()) return false;

        final double newFontHeight = calcNewFontHeight(asciiArtBuilder.getCharWidth(), newCharWidth);
        if (!setAppFontHeight(newFontHeight)) return false;

        asciiArtBuilder.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(asciiArtBuilder.getCharWidth()));
        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
        return true;
    }

    private void applyTheme(ColorTheme colorTheme) {
        Color bgColor = colorTheme.getBGColor();
        asciiArtPane.setBGColor(bgColor);
        bgColorPicker.setValue(bgColor);

        Color textColor = colorTheme.getTextColor();
        asciiArtPane.setTextColor(textColor);
        textColorPicker.setValue(textColor);

        boolean invertedShading = colorTheme.getInvertedShading();
        asciiArtPane.updateArtString(asciiArtBuilder.setInvertedShading(invertedShading).getResult()[0]);
        invertedShadingBtn.setSelected(invertedShading);
    }

    private double calcNewFontHeight(int prevCharWidth, int newCharWidth) {
        final double charWidthChange = ((double) newCharWidth / (double) prevCharWidth);
        final double curFontHeight = Double.parseDouble(fontField.getText());
        return curFontHeight * charWidthChange;
    }

    private void copyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiArtPane.getArtString());
        clipboard.setContent(content);
    }

    private String calculateFileName() {
        String imageName = asciiArtBuilder.getName() == null ? "asciiArtBuilder" : asciiArtBuilder.getName();
        String fileName = imageName + "-cw" + asciiArtBuilder.getCharWidth();
        return asciiArtBuilder.getInvertedShading() ? fileName + "-inv" : fileName;
    }

    private static FileChooser initFileChooser(String title, File initDir, FileChooser.ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(initDir);
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser;
    }

    private boolean save() {
        final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
        FileChooser save = initFileChooser("Save As...", curSaveDir, filter);
        save.setInitialFileName(calculateFileName());

        File output = save.showSaveDialog(new Stage());

        if (output == null) return false;

        try (FileOutputStream fos = new FileOutputStream(output);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);)
        {
            curSaveDir = new File(output.getParent());
            osw.write(asciiArtBuilder.getResult()[0]);

        } catch (IOException e) {
            throw new RuntimeException();
        }
        return true;
    }
*/
}
