package com.york.asciiartstudio.presenter;

import com.york.asciiartstudio.App;
import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.asciiArt.AsciiArt;
import com.york.asciiartstudio.model.asciiArt.AsciiImage;
import com.york.asciiartstudio.view.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


// TODO: This is bloated. Break it up into other classes.
public class IFPresenter {

    @FXML
    public BorderPane body;

    @FXML
    public ToolBar toolBar;

    @FXML
    private MenuButton colorThemeMenu;

    @FXML
    public Button zoomInBtn;

    private AsciiArtPane asciiArtPane;

    @FXML
    public Button chooseImageBtn;

    @FXML
    public TextField charWidthField;

    @FXML
    public TextField fontField;

    @FXML
    public RadioButton invertedShadingBtn;

    @FXML
    public ColorPicker bgColorPicker;

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
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) + 1),
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) - 1),
                () -> setAppCharWidth(asciiImage.getCharWidth() + 1),
                () -> setAppCharWidth(asciiImage.getCharWidth() - 1)
        );

        asciiArtPane = new AsciiArtPane(AsciiArt.FONT);
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));

        for (ColorTheme colorTheme : ColorTheme.values()) {
            colorThemeMenu.getItems().add(new ThemeMenuItem(colorTheme, () -> applyTheme(colorTheme)));
        }
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
    public void openMenuItemClicked() {
        chooseFile();
    }

    @FXML
    public void saveMenuItemClicked() {
        saveAsTxt();
    }

    @FXML
    public void clearMenuItemClicked() {
        asciiImage = null;
        asciiArtPane.setText("");
        fontField.setText("");
        charWidthField.setText("");

        toolBar.setDisable(true);
        body.setCenter(chooseImageBtn);
    }

    @FXML
    public void exitMenuItemClicked() {
        Platform.exit();
    }

    @FXML
    public void copyMenuItemClicked() {
        copyToClipboard();
    }

    @FXML
    public void fontSizeEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;

        double newFontHeight;
        try {
            newFontHeight = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }

        setAppFontHeight(newFontHeight);
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

        if (newCharWidth <= 0) {
            newCharWidth = 1;
        }
        if (newCharWidth > asciiImage.maxCharWidth()) {
            newCharWidth = asciiImage.maxCharWidth();
        }

        setAppCharWidth(newCharWidth);
    }

    @FXML
    public void invertedShadingClicked() {
        asciiImage.setInvertedShading(invertedShadingBtn.isSelected());
        asciiArtPane.setText(asciiImage.toString());
    }

    @FXML
    public void bgColorSelected() {
        asciiArtPane.setBGColor(bgColorPicker.getValue());
    }

    @FXML
    public void textColorSelected() {
        asciiArtPane.setTextColor(textColorPicker.getValue());
    }

    @FXML
    public void copyBtnPressed() {
        copyToClipboard();
    }

    @FXML
    public void saveBtnPressed() {
        saveAsTxt();
    }

    private boolean saveAsTxt() {
        boolean emptyTxt = asciiImage == null;

        FileChooser save = new FileChooser();
        save.setTitle("Save As...");
        save.setInitialDirectory(curSaveDir);
        save.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        if (emptyTxt) {
            save.setInitialFileName("empty-file");
        }
        else {
            String imageName = (asciiImage.getName() == null ? "asciiImage" : asciiImage.getName());
            String txtName = imageName + "-cw" + asciiImage.getCharWidth();

            if (asciiImage.shadingIsInverted()) {
                txtName += "-inv";
            }
            save.setInitialFileName(txtName);
        }

        File output = save.showSaveDialog(new Stage());

        if (output == null) return false;

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
        return true;
    }

    private boolean setAppFontHeight(double newFontHeight) {
        if (!asciiArtPane.setFontSize(newFontHeight)) return false;

        fontField.setText(String.valueOf(newFontHeight));
        return true;
    }

    private boolean setAppCharWidth(int newCharWidth) {
        assert asciiImage != null;
        if (newCharWidth <= 0) return false;
        if (newCharWidth > asciiImage.maxCharWidth()) return false;

        final double newFontHeight = calcNewFontHeight(asciiImage.getCharWidth(), newCharWidth);
        if (!setAppFontHeight(newFontHeight)) return false;

        asciiImage.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        asciiArtPane.setText(asciiImage.toString());
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
        asciiArtPane.setText(asciiImage.setInvertedShading(invertedShading).toString());
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
        content.putString(asciiArtPane.getText());
        clipboard.setContent(content);
    }

    private boolean chooseFile() {

        // File Chooser work
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(curChooseDir);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        fileChooser.setTitle("Select an image");
        File selected = fileChooser.showOpenDialog(new Stage());

        // check user selected file
        if (selected == null) return false;

        ImageSource imageSource;
        try {
            imageSource = new ImageFileAdapter(selected);
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return false;
        }

        if (body.getCenter() != asciiArtPane) {
            body.setCenter(asciiArtPane);
            fontField.setText(String.valueOf(asciiArtPane.getFontSize()));
            charWidthField.setText("1");
        }

        asciiImage = new AsciiImage(imageSource)
                .setInvertedShading(invertedShadingBtn.isSelected())
                .setCharWidth(Integer.parseInt(charWidthField.getText()));

        asciiArtPane.setText(asciiImage.toString());
        toolBar.setDisable(false);
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        curChooseDir = new File(selected.getParent());
        asciiArtPane.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
        return true;
    }
}
