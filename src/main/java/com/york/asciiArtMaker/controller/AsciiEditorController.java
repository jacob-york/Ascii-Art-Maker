package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.DefaultCameraAdapter;
import com.york.asciiArtMaker.model.adapters.ImageFileAdapter;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.view.AsciiViewportPane;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.ThemeMenuItem;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public abstract class AsciiEditorController implements ReturnLocation<VideoSource> {

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

    protected AsciiArtBuilder asciiArtBuilder;
    protected AsciiViewportPane asciiViewportPane;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().select(1);

        asciiViewportPane = new AsciiViewportPane(1400, 770);
        asciiViewportPane.setCursor(Cursor.OPEN_HAND);
        fineZoomBase = asciiViewportPane.getContentZoom();

        borderPane.setCenter(asciiViewportPane);

        for (ColorTheme colorTheme : ColorTheme.values()) {
            colorThemeMenu.getItems().add(new ThemeMenuItem(colorTheme, () -> applyTheme(colorTheme)));
        }

        zoomSlider.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().isArrowKey()) event.consume();
        });

        Scene scene = borderPane.getScene();
        if (scene != null) {
            bindShortcuts(scene);
        } else {
            borderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) bindShortcuts(newScene);
            });
        }

        applyTheme(ColorTheme.NOTEPAD);
    }

    abstract AsciiImage getAsciiImageFrame();

    @FXML
    public boolean newAsciiImageFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectImageFile();
        if (maybeFile.isEmpty()) return false;

        File selectedFile = maybeFile.get();
        ((Stage) borderPane.getScene().getWindow()).close();
        AsciiArtMaker.launchImageEditor(new ImageFileAdapter(selectedFile));

        return true;
    }

    @FXML
    public boolean newAsciiVideoFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectVideoFile();
        if (maybeFile.isEmpty()) return false;

        File selectedFile = maybeFile.get();
        AsciiArtMaker.launchVideoFileConnectionService(selectedFile, this);

        return true;
    }

    @FXML
    public void liveRenderFromCameraChosen() {
        ((Stage) borderPane.getScene().getWindow()).close();
        AsciiArtMaker.launchLiveEditor(new DefaultCameraAdapter());
    }

    @Override
    public void acceptResult(VideoSource result) {
        ((Stage) borderPane.getScene().getWindow()).close();
        AsciiArtMaker.launchVideoEditor(result);
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
        ((Stage) borderPane.getScene().getWindow()).close();
        AsciiArtMaker.launchHome();
    }

    @FXML
    public void quitProject() {
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

    /**
     * @param newCharWidth
     * @return false if unsuccessful (changes are therefore not written),
     * true if successful
     */
    protected void configureAppCharWidth(int newCharWidth)  {
        assert newCharWidth > 0 && newCharWidth <= asciiArtBuilder.getMaxCharWidth();
        asciiArtBuilder.setCharWidth(newCharWidth);

        asciiViewportPane.updateExistingContent(getAsciiImageFrame());
        charWidthField.setText(String.valueOf(newCharWidth));
    }

    @FXML
    public void invertedShadingClicked() {
        asciiArtBuilder.setInvertedShading(invertedShadingButton.isSelected());
        asciiViewportPane.unsafeSetContent(getAsciiImageFrame());
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

    public void applyTheme(ColorTheme colorTheme) {
        Color bgColor = colorTheme.bgColor;
        asciiViewportPane.setBackgroundColor(bgColor);
        bgColorPicker.setValue(bgColor);

        Color textColor = colorTheme.textColor;
        asciiViewportPane.setTextColor(textColor);
        textColorPicker.setValue(textColor);

        boolean invertedShading = colorTheme.invertedShading;
        if (asciiArtBuilder != null && invertedShading != asciiArtBuilder.getInvertedShading()) {
            asciiArtBuilder.setInvertedShading(invertedShading);
            asciiViewportPane.unsafeSetContent(getAsciiImageFrame());
        }
        invertedShadingButton.setSelected(invertedShading);

        activeColorTheme = colorTheme;
        colorThemeMenu.setTextFill(colorTheme.textColor);
        String hexCode = colorTheme.bgColor.toString().substring(2, 8);
        colorThemeMenu.setStyle("-fx-background-color: #" + hexCode + ";");

        setFreestylingColorTheme(false);
    }

}
