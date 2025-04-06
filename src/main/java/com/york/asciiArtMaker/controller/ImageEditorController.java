package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.ImageFileAdapter;
import com.york.asciiArtMaker.model.adapters.ImageSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiImageBuilder;
import com.york.asciiArtMaker.view.AsciiViewportPane;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.ThemeMenuItem;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/* TODO:
 *  File names, artNames, save As Image, Export, Open in text editor, etc.
 */
public class ImageEditorController {

    public static final double ZOOM_TRANSFORM = 0.5;
    public static final double PANNING_TRANSFORM = 75;
    public static final int INIT_CHAR_WIDTH = 5;
    private double mouseXDragSpace;
    private double mouseYDragSpace;

    private ColorTheme activeColorTheme;
    private boolean freestylingColorTheme;

    @FXML
    public BorderPane borderPane;
    @FXML
    public TextField charWidthField;
    @FXML
    public Button charWidthUpButton;
    @FXML
    public Button charWidthDownButton;
    @FXML
    public Slider charWidthSlider;
    @FXML
    public RadioButton invertedShadingButton;
    @FXML
    public ColorPicker bgColorPicker;
    @FXML
    public ColorPicker textColorPicker;
    @FXML
    private MenuButton colorThemeMenu;

    private AsciiViewportPane asciiViewportPane;
    private AsciiImageBuilder asciiImageBuilder;


    @FXML
    public void initialize() {
        asciiViewportPane = new AsciiViewportPane(1400, 770);
        borderPane.setCenter(asciiViewportPane);

        for (ColorTheme colorTheme : ColorTheme.values()) {
            colorThemeMenu.getItems().add(new ThemeMenuItem(colorTheme, () -> applyTheme(colorTheme)));
        }
        charWidthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            charWidthSlider.setValue(newVal.intValue());
            if (asciiViewportPane.hasContent() && !configureAppCharWidth(newVal.intValue()))
                charWidthSlider.setValue(oldVal.intValue());
        });

        Scene scene = borderPane.getScene();
        if (scene != null) {
            bindShortcuts(scene);
        } else {
            borderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    bindShortcuts(newScene);
                }
            });
        }

        applyTheme(ColorTheme.NOTEPAD);
    }

    @FXML
    public void newAsciiImageFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectImageFile();
        if (maybeFile.isPresent()) {
            File selectedFile = maybeFile.get();
            setImageSource(new ImageFileAdapter(selectedFile));
        }
    }

    @FXML
    public void newAsciiVideoFromFileChosen() {

    }

    @FXML
    public void copyToClipboardAction() {
        AsciiArtMaker.copyToClipboard(asciiViewportPane.getContentAsString());
    }

    @FXML
    public void openInTextEditorAction() {
        String art = asciiViewportPane.getContentAsString();
        try {
            Path tempFile = Files.createTempFile(asciiImageBuilder.getFileName(), ".txt");
            Files.write(tempFile, art.getBytes());
            Desktop.getDesktop().edit(tempFile.toFile());
            tempFile.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void saveAsImageAction() {

    }

    @FXML
    public void zoomInAction() {
        asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() + ZOOM_TRANSFORM);
    }
    @FXML
    public void zoomOutAction() {
        asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() - ZOOM_TRANSFORM);
    }

    @FXML
    public void centerContentAction() {
        asciiViewportPane.centerContentPos();
    }


    @FXML
    public void updateCharWidthFromField(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            int newCharWidth;
            try {
                newCharWidth = Integer.parseInt(charWidthField.getText());
                newCharWidth = (int) AsciiArtMaker.ensureInRange(newCharWidth, 1,
                        asciiImageBuilder.getMaxCharWidth());
            } catch (IllegalArgumentException e) {
                newCharWidth = 1;
            }

            if (!configureAppCharWidth(newCharWidth)) {
                charWidthField.setText(String.valueOf(asciiImageBuilder.getCharWidth()));
            }
        }
    }

    @FXML
    public void charWidthUpButtonClicked() {
        if (asciiImageBuilder.getCharWidth() + 1 <= asciiImageBuilder.getMaxCharWidth()) {
            configureAppCharWidth(asciiImageBuilder.getCharWidth() + 1);
        }
    }
    @FXML
    public void charWidthDownButtonClicked() {
        if (asciiImageBuilder.getCharWidth() - 1 > 0) {
            configureAppCharWidth(asciiImageBuilder.getCharWidth() - 1);
        }
    }

    /**
     * @param newCharWidth
     * @return false if unsuccessful (changes are therefore not written),
     * true if successful
     */
    private boolean configureAppCharWidth(int newCharWidth) {
        assert newCharWidth > 0 && newCharWidth <= asciiImageBuilder.getMaxCharWidth();
        asciiImageBuilder.setCharWidth(newCharWidth);

        if (!asciiViewportPane.updateExistingContent(asciiImageBuilder.build())) return false;

        charWidthField.setText(String.valueOf(newCharWidth));
        charWidthSlider.setValue(newCharWidth);

        return true;
    }

    @FXML
    public void invertedShadingClicked() {
        asciiImageBuilder.setInvertedShading(invertedShadingButton.isSelected());
        asciiViewportPane.unsafeSetContent(asciiImageBuilder.build());
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

    private void setFreestylingColorTheme(boolean newValue) {
        if (newValue) {
            colorThemeMenu.setText(activeColorTheme.name + "*");
        } else {
            colorThemeMenu.setText(activeColorTheme.name);
        }

        freestylingColorTheme = newValue;
    }

    public void bindShortcuts(Scene scene) {
        asciiViewportPane.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                mouseXDragSpace = mouseEvent.getSceneX() - asciiViewportPane.getContentXPos();
                mouseYDragSpace = mouseEvent.getSceneY() - asciiViewportPane.getContentYPos();
            }
        });
        asciiViewportPane.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                asciiViewportPane.setContentXPos(mouseEvent.getSceneX() - mouseXDragSpace);
                asciiViewportPane.setContentYPos(mouseEvent.getSceneY() - mouseYDragSpace);
            }
        });

        asciiViewportPane.addEventFilter(ScrollEvent.SCROLL, scrollEvent -> {
            if (scrollEvent.isAltDown()) {
                double zoomTransform = scrollEvent.getDeltaY() > 0 ? ZOOM_TRANSFORM : (-1 * ZOOM_TRANSFORM);
                asciiViewportPane.setContentZoom(asciiViewportPane.getContentZoom() + zoomTransform);
                scrollEvent.consume();
            } else if (scrollEvent.isControlDown()) {
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

        KeyCombination kbCopyToClipboard = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(kbCopyToClipboard, this::copyToClipboardAction);
    }

    public void setImageSource(ImageSource imageSource) {
        asciiImageBuilder = new AsciiImageBuilder(imageSource)
                .setCharWidth(INIT_CHAR_WIDTH)
                .setInvertedShading(activeColorTheme.invertedShading);
        charWidthSlider.setMax(Math.floor(asciiImageBuilder.getMaxCharWidth() / 16.0));
        charWidthSlider.setValue(INIT_CHAR_WIDTH);
        charWidthField.setText(String.valueOf(INIT_CHAR_WIDTH));
        asciiViewportPane.setNewContent(asciiImageBuilder.build());
    }

    public void applyTheme(ColorTheme colorTheme) {
        Color bgColor = colorTheme.bgColor;
        asciiViewportPane.setBackgroundColor(bgColor);
        bgColorPicker.setValue(bgColor);

        Color textColor = colorTheme.textColor;
        asciiViewportPane.setTextColor(textColor);
        textColorPicker.setValue(textColor);

        boolean invertedShading = colorTheme.invertedShading;
        if (asciiImageBuilder != null && invertedShading != asciiImageBuilder.getInvertedShading()) {
            asciiImageBuilder.setInvertedShading(invertedShading);
            asciiViewportPane.unsafeSetContent(asciiImageBuilder.build());
        }
        invertedShadingButton.setSelected(invertedShading);

        activeColorTheme = colorTheme;
        colorThemeMenu.setTextFill(colorTheme.textColor);
        String hexCode = colorTheme.bgColor.toString().substring(2, 8);
        colorThemeMenu.setStyle("-fx-background-color: #" + hexCode + ";");

        setFreestylingColorTheme(false);
    }
}
