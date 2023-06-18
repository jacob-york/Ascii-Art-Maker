package com.york.asciiartstudio.controller;

import com.york.asciiartstudio.App;
import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.asciiArt.AsciiArt;
import com.york.asciiartstudio.model.asciiArt.AsciiImage;
import com.york.asciiartstudio.view.AsciiImagePane;
import com.york.asciiartstudio.view.TimelineContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;


// TODO: break up into multiple classes (this is bad)
public class IFMController {

    @FXML
    public BorderPane body;
    @FXML
    public ToolBar toolBar;

    @FXML
    public Button zoomInBtn;

    private AsciiImagePane asciiImagePane;

    @FXML
    public Button chooseImageBtn;

    @FXML
    public TextField charWidthField;

    @FXML
    public TextField fontField;

    @FXML
    public RadioButton invertedShadingBtn;

    @FXML
    public ColorPicker bGColorPicker;

    @FXML
    public ColorPicker textColorPicker;

    @FXML
    public MenuItem openMenuItem;

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem copyMenuItem;

    private AsciiImage asciiImage;

    private TimelineContainer timelines;

    private File curChooseDir;

    private File curSaveDir;

    @FXML
    public void initialize() {
        fontField.setText("");
        charWidthField.setText("");

        timelines = new TimelineContainer(
                () -> {
                    // zoom in behavior
                    double fontHeight = Double.parseDouble(fontField.getText());

                    asciiImagePane.setFontSize(++fontHeight);
                    fontField.setText(String.valueOf(fontHeight));
                },
                () -> {
                    // zoom out behavior
                    double fontHeight = Double.parseDouble(fontField.getText());

                    if (!asciiImagePane.setFontSize(--fontHeight)) return;
                    fontField.setText(String.valueOf(fontHeight));
                },
                () -> {
                    // raise char width behavior
                    assert asciiImage != null;

                    int oldCharWidth = asciiImage.getCharWidth();
                    if (oldCharWidth + 1 > asciiImage.getMaxCharWidth()) return;
                    int newCharWidth = oldCharWidth + 1;
                    updateCharWidth(newCharWidth);

                    double newFontHeight = newFontSize(oldCharWidth, newCharWidth);
                    asciiImagePane.setFontSize(newFontHeight);
                    fontField.setText(String.valueOf(newFontHeight));
                },
                () -> {
                    // lower char width behavior
                    assert asciiImage != null;

                    int oldCharWidth = asciiImage.getCharWidth();
                    if (oldCharWidth - 1 <= 0) return;
                    int newCharWidth = oldCharWidth - 1;
                    double newFontHeight = newFontSize(oldCharWidth, newCharWidth);
                    if (!asciiImagePane.setFontSize(newFontHeight)) return;

                    updateCharWidth(newCharWidth);
                    fontField.setText(String.valueOf(newFontHeight));
                }
        );

        asciiImagePane = new AsciiImagePane(AsciiArt.FONT);
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));

    }

    private double newFontSize(int prevCharWidth, int newCharWidth) {
        final double fontHeightChange = ((double) newCharWidth / (double) prevCharWidth);
        final double fontHeight = Double.parseDouble(fontField.getText());
        return fontHeight * fontHeightChange;
    }

    public void zoomInPressed() {
        timelines.zoomIn();
    }

    public void zoomInReleased() {
        timelines.stopZoom();
    }

    public void zoomOutPressed() {
        timelines.zoomOut();
    }

    public void zoomOutReleased() {
        timelines.stopZoom();
    }

    public void charWidthUpPressed() {
        timelines.charWidthUp();
    }

    public void charWidthUpReleased() {
        timelines.stopCharWidth();
    }

    public void charWidthDownPressed() {
        timelines.charWidthDown();
    }

    public void charWidthDownReleased() {
        timelines.stopCharWidth();
    }

    public void chooseImageBtnClicked() {
        chooseFile();
    }

    public void chooseFile() {

        // File Chooser work
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(curChooseDir);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        fileChooser.setTitle("Select an image");
        File selected = fileChooser.showOpenDialog(new Stage());

        // check user selected file
        if (selected == null) return;

        ImageSource imageSource;
        try {
            imageSource = new ImageFileAdapter(selected);
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return;
        }

        // init art
        if (body.getCenter() != asciiImagePane) {
            body.setCenter(asciiImagePane);
            fontField.setText(String.valueOf(asciiImagePane.getFontSize()));
            charWidthField.setText("1");
        }
        asciiImage = new AsciiImage(imageSource)
                .setInvertedShading(invertedShadingBtn.isSelected())
                .setCharWidth(Integer.parseInt(charWidthField.getText()));
        asciiImagePane.setText(asciiImage.toString());
        toolBar.setDisable(false);
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        curChooseDir = new File(selected.getParent());
        asciiImagePane.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
    }

    public void copyPressed() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiImagePane.getText());
        clipboard.setContent(content);
    }

    public void exportAsTxtPressed() {
        boolean emptyTxt = asciiImage == null;

        FileChooser save = new FileChooser();
        save.setTitle("Save As...");
        save.setInitialDirectory(curSaveDir);
        save.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        if (emptyTxt) save.setInitialFileName("empty-file");
        else save.setInitialFileName(calculateName());
        File output = save.showSaveDialog(new Stage());

        if (output == null) return;

        try {
            curSaveDir = new File(output.getParent());
            FileOutputStream fos = new FileOutputStream(output);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            if (emptyTxt) osw.write("");
            else osw.write(asciiImage.toString());

            osw.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void fontSizeEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;

        double newFontSize;
        try {
            newFontSize = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        if (!asciiImagePane.setFontSize(newFontSize)) return;
        fontField.setText(new DecimalFormat("#.0").format(newFontSize));
    }

    public void charWidthEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;
        assert asciiImage != null;

        int oldCharWidth = asciiImage.getCharWidth();
        int newCharWidth;
        try {
            newCharWidth = Integer.parseInt(charWidthField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }

        if (newCharWidth <= 0) {
            charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
            return;
        }

        if (newCharWidth > asciiImage.getMaxCharWidth()) {
            newCharWidth = asciiImage.getMaxCharWidth();
        }

        double newFontHeight = newFontSize(oldCharWidth, newCharWidth);
        if (!asciiImagePane.setFontSize(newFontHeight)) return;
        updateCharWidth(newCharWidth);
        fontField.setText(String.valueOf(newFontHeight));
    }

    public void invertedShadingClicked() {
        asciiImage.setInvertedShading(invertedShadingBtn.isSelected());
        asciiImagePane.setText(asciiImage.toString());
    }

    public void exitClicked() {
        Platform.exit();
    }

    public void bgColorSelected() {
        asciiImagePane.setBGColor(bGColorPicker.getValue());
    }

    public void textColorSelected() {
        asciiImagePane.setTextColor(textColorPicker.getValue());
    }

    public void clearImageClicked() {
        asciiImage = null;
        asciiImagePane.setText("");
        fontField.setText("");
        charWidthField.setText("");

        toolBar.setDisable(true);
        body.setCenter(chooseImageBtn);
    }

    private String calculateName() {
        String name = (asciiImage.getName() == null ? "asciiImage" : asciiImage.getName());
        String outputPath = name + "-" + asciiImage.getWidth();

        if (asciiImage.shadingIsInverted()) {
            outputPath += "-inv";
        }
        return outputPath;
    }

    private void updateCharWidth(int newCharWidth) {
        asciiImage.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        asciiImagePane.setText(asciiImage.toString());
    }

}
