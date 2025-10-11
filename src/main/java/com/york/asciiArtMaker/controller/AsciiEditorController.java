package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.ImageFileAdapter;
import com.york.asciiArtMaker.model.adapters.ScreenCaptureAdapter;
import com.york.asciiArtMaker.model.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.view.AsciiViewportPane;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.ThemeMenuItem;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Optional;

public abstract class AsciiEditorController implements Closable {

    public static final double ZOOM_TRANSFORM = 0.25;
    public static final double PANNING_TRANSFORM = 50;
    public static final int INIT_CHAR_WIDTH = 5;

    protected ColorTheme activeColorTheme;
    protected boolean freestylingColorTheme;
    protected double fineZoomBase;

    @FXML
    public BorderPane borderPane;
    @FXML
    public TabPane tabPane;
    @FXML
    public Tab fileTab;
    @FXML
    public Slider zoomSlider;
    @FXML
    public TextField charWidthField;
    @FXML
    public Button charWidthUpButton;
    @FXML
    public Button charWidthDownButton;
    @FXML
    public RadioButton invertedShadingButton;
    @FXML
    public ColorPicker bgColorPicker;
    @FXML
    public ColorPicker textColorPicker;
    @FXML
    public MenuButton colorThemeMenu;
    @FXML
    public CheckBox fullScreenCheckBox;

    protected AsciiArtBuilder asciiArtBuilder;
    protected AsciiViewportPane asciiViewportPane;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().select(1);

        asciiViewportPane = new AsciiViewportPane(1400, 770);
        asciiViewportPane.setCursor(Cursor.OPEN_HAND);
        fineZoomBase = asciiViewportPane.getContentZoom();
        freestylingColorTheme = false;
        borderPane.setCenter(asciiViewportPane);

        for (ColorTheme colorTheme : ColorTheme.values()) {
            colorThemeMenu.getItems().add(new ThemeMenuItem(colorTheme, () -> {
                styleView(colorTheme);
                boolean newInvertedShading = colorTheme.invertedShading;
                if (newInvertedShading != activeColorTheme.invertedShading) {
                    configureAppInvertedShading(newInvertedShading);
                }
                activeColorTheme = colorTheme;
                setFreestylingColorTheme(false);
            }));
        }

        zoomSlider.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().isArrowKey()) event.consume();
        });

        Scene scene = borderPane.getScene();

        if (borderPane.getScene() != null) {
            bindShortcuts(scene);
            ((Stage) scene.getWindow())
                    .fullScreenProperty()
                    .addListener((obs, oldValue, newValue) -> fullScreenCheckBox.setSelected(newValue));
        } else {
            borderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    bindShortcuts(newScene);
                    ((Stage) newScene.getWindow())
                            .fullScreenProperty()
                            .addListener((obs1, oldValue, newValue) -> fullScreenCheckBox.setSelected(newValue));
                }
            });
        }

        styleView(ColorTheme.NOTEPAD);
        activeColorTheme = ColorTheme.NOTEPAD;
    }

    abstract AsciiImage getAsciiImageFrame();

    @FXML
    public boolean newAsciiImageFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectImageFile();
        if (maybeFile.isEmpty()) return false;

        close();

        File selectedFile = maybeFile.get();
        AsciiArtMaker.launchImageEditor(new ImageFileAdapter(selectedFile));

        return true;
    }

    @FXML
    public boolean newAsciiVideoFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectVideoFile();
        if (maybeFile.isEmpty()) return false;

        File selectedFile = maybeFile.get();
        AsciiArtMaker.launchVideoFileConnectionTask(selectedFile, this);

        return true;
    }

    @FXML
    public void liveRenderFromCameraChosen() {
        AsciiArtMaker.launchWebcamConnectionTask(this);
    }

    @FXML
    public void liveRenderFromScreenCaptureChosen() {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        Robot robot = null;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        } catch (SecurityException e) {
            new Alert(Alert.AlertType.ERROR, "Permission to capture screen was denied by your security manager:\n" + e.getMessage()).showAndWait();
        }

        if (robot != null) {
            stage.close();
            AsciiArtMaker.launchLiveEditor(new ScreenCaptureAdapter(robot));
        }
    }

    @FXML
    public void copyToClipboardAction() {
        AsciiArtMaker.copyToClipboard(asciiViewportPane.getContentAsString());
    }

    @FXML
    public void exportAsImageAction() {
        // todo
    }

    @FXML
    public void closeEditorAction() {
        close();
        AsciiArtMaker.launchHome();
    }

    @FXML
    public void quitProject() {
        close();
        System.exit(0);
    }

    @FXML
    public void zoomInAction() {
        asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() + AsciiViewportPane.ZOOM_TRANSFORM);
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);
    }
    @FXML
    public void zoomOutAction() {
        asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() - AsciiViewportPane.ZOOM_TRANSFORM);
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);
    }

    @FXML
    public void fineZoomAction() {
        if (!asciiViewportPane.setContentZoom(fineZoomBase + zoomSlider.getValue())) {
            zoomSlider.setValue(asciiViewportPane.getContentZoom() - fineZoomBase);
        }
    }

    @FXML
    public void fitToAreaAction() {
        asciiViewportPane.fitContentToArea();
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);
    }

    @FXML
    public void toggleFullScreen() {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    public void updateCharWidthFromField(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            int newCharWidth;
            try {
                newCharWidth = Integer.parseInt(charWidthField.getText());
                newCharWidth = (int) AsciiArtMaker.ensureInRange(newCharWidth, 1,
                        asciiArtBuilder.getMaxCharWidth());
            } catch (IllegalArgumentException e) {
                newCharWidth = 1;
            }

            configureAppCharWidth(newCharWidth);
        }
    }

    @FXML
    public void charWidthUpButtonClicked() {
        if (asciiArtBuilder.getCharWidth() + 1 <= asciiArtBuilder.getMaxCharWidth()) {
            configureAppCharWidth(asciiArtBuilder.getCharWidth() + 1);
        }
    }
    @FXML
    public void charWidthDownButtonClicked() {
        if (asciiArtBuilder.getCharWidth() - 1 > 0) {
            configureAppCharWidth(asciiArtBuilder.getCharWidth() - 1);
        }
    }

    protected void configureAppCharWidth(int newCharWidth)  {
        assert newCharWidth > 0 && newCharWidth <= asciiArtBuilder.getMaxCharWidth();
        asciiArtBuilder.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(newCharWidth));
    }
    protected void configureAppInvertedShading(boolean newInvertedShading) {
        asciiArtBuilder.setInvertedShading(newInvertedShading);
    }

    @FXML
    public void invertedShadingClicked() {
        configureAppInvertedShading(invertedShadingButton.isSelected());
    }

    @FXML
    public void bgColorSelected() {
        asciiViewportPane.setBackgroundColor(bgColorPicker.getValue());

        if (textColorPicker.getValue().equals(activeColorTheme.textColor) &&
                bgColorPicker.getValue().equals(activeColorTheme.bgColor) && freestylingColorTheme) {
            setFreestylingColorTheme(false);
        } else if (!bgColorPicker.getValue().equals(activeColorTheme.bgColor) && !freestylingColorTheme) {
            setFreestylingColorTheme(true);
        }
    }

    @FXML
    public void textColorSelected() {
        asciiViewportPane.setTextColor(textColorPicker.getValue());

        if (textColorPicker.getValue().equals(activeColorTheme.textColor) &&
                bgColorPicker.getValue().equals(activeColorTheme.bgColor) && freestylingColorTheme) {
            setFreestylingColorTheme(false);
        } else if (!textColorPicker.getValue().equals(activeColorTheme.textColor) && !freestylingColorTheme) {
            setFreestylingColorTheme(true);
        }
    }

    protected void setFreestylingColorTheme(boolean newValue) {
        if (newValue) {
            colorThemeMenu.setText(activeColorTheme.name + "*");
        } else {
            colorThemeMenu.setText(activeColorTheme.name);
        }

        freestylingColorTheme = newValue;
    }

    public void bindShortcuts(Scene scene) {
        asciiViewportPane.addEventFilter(ScrollEvent.SCROLL, scrollEvent -> {
            if (scrollEvent.isAltDown()) {
                double zoomTransform = scrollEvent.getDeltaY() > 0 ? ZOOM_TRANSFORM : (-1 * ZOOM_TRANSFORM);
                asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() + zoomTransform);
                fineZoomBase = asciiViewportPane.getContentZoom();
                zoomSlider.setValue(0);
            }
            else if (scrollEvent.isControlDown()) {
                double xPosTransform = scrollEvent.getDeltaY() > 0 ? PANNING_TRANSFORM : (-1 * PANNING_TRANSFORM);
                asciiViewportPane.setContentXPos(asciiViewportPane.getContentXPos() + xPosTransform);
                scrollEvent.consume();
            } else {
                double yPosTransform = scrollEvent.getDeltaY() > 0 ? PANNING_TRANSFORM : (-1 * PANNING_TRANSFORM);
                asciiViewportPane.setContentYPos(asciiViewportPane.getContentYPos() + yPosTransform);
                scrollEvent.consume();
            }
        });

        KeyCombination kbZoomIn = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(kbZoomIn, this::zoomInAction);

        KeyCombination kbZoomOut = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(kbZoomOut, this::zoomOutAction);
    }

    protected void styleView(ColorTheme colorTheme) {
        Color bgColor = colorTheme.bgColor;
        asciiViewportPane.setBackgroundColor(bgColor);
        bgColorPicker.setValue(bgColor);

        Color textColor = colorTheme.textColor;
        asciiViewportPane.setTextColor(textColor);
        textColorPicker.setValue(textColor);

        invertedShadingButton.setSelected(colorTheme.invertedShading);

        colorThemeMenu.setTextFill(colorTheme.textColor);
        String hexCode = colorTheme.bgColor.toString().substring(2, 8);
        colorThemeMenu.setStyle("-fx-background-color: #" + hexCode + ";");
    }

    @Override
    public void close() {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();
    }
}
