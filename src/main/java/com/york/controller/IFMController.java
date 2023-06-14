package com.york.controller;

import com.york.App;
import com.york.model.Settings;
import com.york.model.adapters.ImageFileAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiArt;
import com.york.model.asciiArt.AsciiImage;
import com.york.view.AsciiViewPane;
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


// TODO: break up responsibility to multiple classes

public class IFMController {

    @FXML
    public BorderPane body;
    @FXML
    public ToolBar toolBar;

    private AsciiViewPane asciiViewPane;

    @FXML
    public Button chooseImageBtn;

    // charWidth
    @FXML
    public TextField artWidthField;

    // zoom
    @FXML
    public TextField fontField;

    // invertedShading
    @FXML
    public RadioButton invertedShadingBtn;

    // color pickers
    @FXML
    public ColorPicker bGColorPicker;
    @FXML
    public ColorPicker textColorPicker;

    // Menu Items
    @FXML
    public MenuItem openMenuItem;
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem copyMenuItem;

    private AsciiImage asciiImage;
    private ImageSource imageSource;


    @FXML
    public void initialize() {
        fontField.setText("");
        artWidthField.setText("");

        asciiViewPane = new AsciiViewPane(AsciiArt.FONT);
        asciiViewPane.setOnScroll((ScrollEvent event) -> {
            if (event.getDeltaY() == 0) return;
            if (imageSource == null && asciiImage == null) return;

            if (event.getDeltaY() < 0) {
                zoomIn();
            }
            else {
                zoomOut();
            }
        });

        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
    }

    private String calculateName() {
        String name = (asciiImage.getName() == null ? "asciiImage" : asciiImage.getName());
        String outputPath = name + "-" + asciiImage.getWidth();
        if (asciiImage.shadingIsInverted()) {
            outputPath += "-inv";
        }
        return outputPath;
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
        }
        asciiImage = new AsciiImage(imageSource);
        setInvertedShading();

        toolBar.setDisable(false);
        artWidthField.setText(String.valueOf(asciiImage.getWidth()));
        Settings.getInstance().setCurChooseDir(new File(selected.getParent()));
        asciiViewPane.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
    }

    @FXML
    public void zoomIn() {
       double fontSize = Double.parseDouble(fontField.getText());

       asciiViewPane.setFontSize(++fontSize);
       fontField.setText(String.valueOf(fontSize));
    }

    @FXML
    public void zoomOut() {
        double fontSize = Double.parseDouble(fontField.getText());

        if (!asciiViewPane.setFontSize(--fontSize)) return;
        fontField.setText(String.valueOf(fontSize));
    }

    @FXML
    public void setFontFromField(KeyEvent keyEvent) {
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

    private void updateCharWidth(int newCharWidth) {
        asciiImage.setCharWidth(newCharWidth);
        artWidthField.setText(String.valueOf(asciiImage.getWidth()));
        asciiViewPane.setText(asciiImage.toString());
    }

    @FXML
    public void raiseArtWidth() {
        assert asciiImage != null;

        int newCharWidth = asciiImage.getCharWidth();
        if (newCharWidth - 1 <= 0) return;
        newCharWidth--;

        updateCharWidth(newCharWidth);
    }

    @FXML
    public void raiseCharWidth() {
        assert asciiImage != null;

        int newCharWidth = asciiImage.getCharWidth();
        newCharWidth++;

        updateCharWidth(newCharWidth);
    }

    @FXML
    public void charWidthFromField(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) {
            return;
        }
        assert asciiImage != null;

        int newCharWidth;
        try {
            newCharWidth = Integer.parseInt(artWidthField.getText());
        } catch (NumberFormatException e) {
            return;
        }

        if (newCharWidth <= 0) {
            return;
        }
        if (newCharWidth > asciiImage.getMaxCharWidth()) {
            newCharWidth = asciiImage.getMaxCharWidth();
        }

        updateCharWidth(newCharWidth);
    }

    @FXML
    public void copyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiViewPane.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void saveAsTxt() {
        boolean emptyTxt = imageSource == null && asciiImage == null;

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
    public void setInvertedShading() {
        asciiImage.setInvertedShading(invertedShadingBtn.isSelected());
        asciiViewPane.setText(asciiImage.toString());
    }

    @FXML
    public void stopRequest() {
        Platform.exit();
    }

    @FXML
    public void setBGColor() {
        asciiViewPane.setBGColor(bGColorPicker.getValue());
    }

    @FXML
    public void setTextColor() {
        asciiViewPane.setTextColor(textColorPicker.getValue());
    }

    @FXML
    public void clearImage() {
        asciiImage = null;
        imageSource = null;

        asciiViewPane.setText("");
        fontField.setText("");
        artWidthField.setText("");

        toolBar.setDisable(true);
        body.setCenter(chooseImageBtn);
    }
}
