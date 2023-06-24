package com.york.asciiartstudio.presenter;

import com.york.asciiartstudio.App;
import com.york.asciiartstudio.model.AppModel;
import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.view.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


// TODO: This is bloated. Break it up into other classes.
public class homePresenter {

    @FXML
    public BorderPane borderPane;

    @FXML
    public ToolBar toolBar;

    @FXML
    private MenuButton colorThemeMenu;

    @FXML
    public Button zoomInBtn;

    private AsciiArtPane asciiArtPane;

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
    public MenuItem openImageItem;

    @FXML
    public MenuItem openVideoItem;

    @FXML
    public HBox videoControlBox;

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem copyMenuItem;

    private AppModel model;

    private TimelineContainer timelines;

    @FXML
    public void initialize() {
        this.model = new AppModel();

        fontField.setText("");
        charWidthField.setText("");

        timelines = new TimelineContainer(
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) + 1),
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) - 1),
                () -> setAppCharWidth(model.getCharWidth() + 1),
                () -> setAppCharWidth(model.getCharWidth() - 1)
        );

        asciiArtPane = new AsciiArtPane("Consolas");

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
    public void openImageItemClicked() {
        chooseImageFile();
    }

    @FXML
    public void openVideoItemClicked() {
    }

    @FXML
    public void webcamMenuItemClicked() {
    }

    @FXML
    public void saveMenuItemClicked() {
        saveAsTxt();
    }

    @FXML
    public void clearMenuItemClicked() {
        clear();
    }

    @FXML
    public void exitMenuItemClicked() {
        Platform.exit();
    }

    @FXML
    public void copyMenuItemClicked() {
        AppModel.copyToClipboard(asciiArtPane.getArtString());
    }

    @FXML
    public void fontHeightEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;
        fontFromField();
    }

    @FXML
    public void charWidthEntered(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;
        charWidthFromField();
    }

    @FXML
    public void invertedShadingClicked() {
        setAppInvertedShading();
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
        AppModel.copyToClipboard(asciiArtPane.getArtString());
    }

    @FXML
    public void saveBtnPressed() {
        saveAsTxt();
    }

    @FXML
    public void centerBtnPressed() {
    }

    @FXML
    public void centerBtnReleased() {
    }

    public void fontFromField() {
        double newVal;
        try {
            newVal = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        setAppFontHeight(AppModel.ensureRange(newVal, 1, Integer.MAX_VALUE));
    }

    public void charWidthFromField() {
        int newVal;
        try {
            newVal = Integer.parseInt(charWidthField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        setAppCharWidth(newVal);
    }

    public void clear() {
        model.clearArtBuilder();
        asciiArtPane.updateArtString("");
        fontField.setText("");
        charWidthField.setText("");

        disableToolBar();
        borderPane.setCenter(new Label("No Media Selected."));
    }

    public void disableToolBar() {
        toolBar.setDisable(true);
        copyMenuItem.setDisable(true);
        saveMenuItem.setDisable(true);
        videoControlBox.setDisable(true);
    }

    public boolean setAppFontHeight(double newFontHeight) {
        if (!asciiArtPane.setFontSize(newFontHeight)) return false;

        fontField.setText(String.valueOf(newFontHeight));
        return true;
    }

    public boolean setAppCharWidth(int newCharWidth) {
        final double newFontHeight = model.calcNewFontHeight(newCharWidth, Double.parseDouble(fontField.getText()));
        if (!setAppFontHeight(newFontHeight)) return false;

        newCharWidth = model.setCharWidth(newCharWidth);

        charWidthField.setText(String.valueOf(newCharWidth));
        asciiArtPane.updateArtString(model.queryArt());
        return true;
    }

    public void setAppInvertedShading() {
        model.setInvertedShading(invertedShadingBtn.isSelected());
        asciiArtPane.updateArtString(model.queryArt());
    }

    public void applyTheme(ColorTheme colorTheme) {
        Color bgColor = colorTheme.getBGColor();
        asciiArtPane.setBGColor(bgColor);
        bgColorPicker.setValue(bgColor);

        Color textColor = colorTheme.getTextColor();
        asciiArtPane.setTextColor(textColor);
        textColorPicker.setValue(textColor);

        boolean invertedShading = colorTheme.getInvertedShading();
        model.setInvertedShading(invertedShading);
        asciiArtPane.updateArtString(model.queryArt());
        invertedShadingBtn.setSelected(invertedShading);
    }

    public boolean saveAsTxt() {
        final FileChooser.ExtensionFilter[] filters = new FileChooser.ExtensionFilter[] {
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        };
        File output = View.saveFile("Save As...", model.getCurSaveDir(), filters, model.calculateFileName());

        return model.saveAsTxt(output);
    }

    public void enableTBForVideo() {
        toolBar.setDisable(false);
        copyMenuItem.setDisable(false);
        saveMenuItem.setDisable(false);
        videoControlBox.setDisable(false);
    }

    public void enableTBForImage() {
        toolBar.setDisable(false);
        copyMenuItem.setDisable(false);
        saveMenuItem.setDisable(false);
    }

    public boolean chooseImageFile() {
        final FileChooser.ExtensionFilter[] filters = new FileChooser.ExtensionFilter[] {
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        };
        final boolean viewRequiresInit = model.artBuilderIsNull();

        if (viewRequiresInit) {
            enableTBForImage();
            borderPane.setCenter(asciiArtPane);
            fontField.setText(String.valueOf(asciiArtPane.getFontSize()));
            charWidthField.setText("1");
        }

        if (!model.setImage(
                View.selectFile("Select an Image...", model.getCurChooseDir(), filters),
                Integer.parseInt(charWidthField.getText()),
                invertedShadingBtn.isSelected()
        )) return false;

        charWidthField.setText(String.valueOf(model.getCharWidth()));
        asciiArtPane.updateArtString(model.queryArt());
        ((Stage) borderPane.getScene().getWindow()).setTitle(model.getMediaName() + " - " + App.TITLE);

        return true;
    }

}
