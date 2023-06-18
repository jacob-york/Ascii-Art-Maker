package com.york.asciiartstudio.controller;

import com.york.asciiartstudio.App;
import com.york.asciiartstudio.model.Settings;
import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.asciiArt.AsciiArt;
import com.york.asciiartstudio.model.asciiArt.AsciiImage;
import com.york.asciiartstudio.view.AsciiViewPane;
import com.york.asciiartstudio.view.TimelineManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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


public class IFMController {

    @FXML
    public BorderPane body;
    @FXML
    public ToolBar toolBar;

    @FXML
    public Button zoomInBtn;

    private AsciiViewPane asciiViewPane;

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

    private TimelineManager timelines;

    @FXML
    public void initialize() {
        fontField.setText("");
        charWidthField.setText("");

        timelines = new TimelineManager(
                () -> {
                    // zoom in behavior
                    double fontSize = Double.parseDouble(fontField.getText());
                    asciiViewPane.setFontSize(++fontSize);
                    fontField.setText(String.valueOf(fontSize));
                },
                () -> {
                    // zoom out behavior
                    double fontSize = Double.parseDouble(fontField.getText());
                    if (!asciiViewPane.setFontSize(--fontSize)) return;
                    fontField.setText(String.valueOf(fontSize));
                },
                () -> {
                    // raise char width behavior
                    assert asciiImage != null;
                    int newCharWidth = asciiImage.getCharWidth();
                    if (newCharWidth + 1 > asciiImage.getMaxCharWidth()) return;
                    updateCharWidth(++newCharWidth);
                },
                () -> {
                    // lower char width behavior
                    assert asciiImage != null;
                    int newCharWidth = asciiImage.getCharWidth();
                    if (newCharWidth - 1 <= 0) return;
                    updateCharWidth(--newCharWidth);
                }
        );

        asciiViewPane = new AsciiViewPane(AsciiArt.FONT);
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
    }

    @FXML
    public void zoomInPressed() {
        timelines.zoomIn();
    }

    @FXML
    public void zoomInReleased() {
        timelines.stopZoom();
    }

    @FXML
    public void zoomOutPressed() {
        timelines.zoomOut();
    }

    @FXML
    public void zoomOutReleased() {
        timelines.stopZoom();
    }

    @FXML
    public void charWidthUpPressed() {
        timelines.charWidthUp();
    }

    @FXML
    public void charWidthUpReleased() {
        timelines.stopCharWidth();
    }

    @FXML
    public void charWidthDownPressed() {
        timelines.charWidthDown();
    }

    @FXML
    public void charWidthDownReleased() {
        timelines.stopCharWidth();
    }

    @FXML
    public void chooseImageBtnClicked() {
        chooseFile();
    }

    @FXML
    public void chooseFile() {

        // File Chooser work
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Settings.getInstance().getCurChooseDir());
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
        if (body.getCenter() != asciiViewPane) {
            body.setCenter(asciiViewPane);
            fontField.setText(String.valueOf(asciiViewPane.getFontSize()));
            charWidthField.setText("1");
        }
        asciiImage = new AsciiImage(imageSource)
                .setInvertedShading(invertedShadingBtn.isSelected())
                .setCharWidth(Integer.parseInt(charWidthField.getText()));
        asciiViewPane.setText(asciiImage.toString());
        toolBar.setDisable(false);
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        Settings.getInstance().setCurChooseDir(new File(selected.getParent()));
        asciiViewPane.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
    }

    @FXML
    public void copyPressed() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiViewPane.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void exportAsTxtPressed() {
        boolean emptyTxt = asciiImage == null;

        FileChooser save = new FileChooser();
        save.setTitle("Save As...");
        save.setInitialDirectory(Settings.getInstance().getCurSaveDir());
        save.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        if (emptyTxt) save.setInitialFileName("empty-file");
        else save.setInitialFileName(calculateName());
        File output = save.showSaveDialog(new Stage());

        if (output == null) return;

        try {
            Settings.getInstance().setCurSaveDir(new File(output.getParent()));
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

    @FXML
    public void fontSizeEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;

        double newFontSize;
        try {
            newFontSize = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        if (!asciiViewPane.setFontSize(newFontSize)) return;
        fontField.setText(new DecimalFormat("#.0").format(newFontSize));
    }

    @FXML
    public void charWidthEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;
        assert asciiImage != null;

        int newCharWidth;
        try {
            newCharWidth = Integer.parseInt(charWidthField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }

        if (newCharWidth <= 0) return;

        if (newCharWidth > asciiImage.getMaxCharWidth()) {
            newCharWidth = asciiImage.getMaxCharWidth();
        }

        updateCharWidth(newCharWidth);
    }

    @FXML
    public void invertedShadingClicked() {
        asciiImage.setInvertedShading(invertedShadingBtn.isSelected());
        asciiViewPane.setText(asciiImage.toString());
    }

    @FXML
    public void exitClicked() {
        Platform.exit();
    }

    @FXML
    public void bgColorSelected() {
        asciiViewPane.setBGColor(bGColorPicker.getValue());
    }

    @FXML
    public void textColorSelected() {
        asciiViewPane.setTextColor(textColorPicker.getValue());
    }

    @FXML
    public void clearImageClicked() {
        asciiImage = null;
        asciiViewPane.setText("");
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
        asciiViewPane.setText(asciiImage.toString());
    }
}
