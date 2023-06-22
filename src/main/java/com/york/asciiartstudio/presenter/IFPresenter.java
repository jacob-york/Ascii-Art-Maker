package com.york.asciiartstudio.presenter;

import com.york.asciiartstudio.App;
import com.york.asciiartstudio.model.adapters.ImageFileAdapter;
import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.adapters.VideoFileAdapter;
import com.york.asciiartstudio.model.adapters.VideoSource;
import com.york.asciiartstudio.model.asciiArt.AsciiArtBuilder;
import com.york.asciiartstudio.model.asciiArt.AsciiImageBuilder;
import com.york.asciiartstudio.model.asciiArt.AsciiVideoBuilder;
import com.york.asciiartstudio.model.appState.AppState;
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
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem copyMenuItem;

    private AsciiArtBuilder asciiArtBuilder;

    private TimelineContainer timelines;

    private AppState appState;

    private File curChooseDir;

    private File curSaveDir;

    @FXML
    public void initialize() {
        fontField.setText("");
        charWidthField.setText("");

        timelines = new TimelineContainer(
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) + 1),
                () -> setAppFontHeight(Double.parseDouble(fontField.getText()) - 1),
                () -> setAppCharWidth(asciiArtBuilder.getCharWidth() + 1),
                () -> setAppCharWidth(asciiArtBuilder.getCharWidth() - 1)
        );

        asciiArtPane = new AsciiArtPane(AsciiArtBuilder.FONT);

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
        chooseVideoFile();
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
        asciiArtBuilder = null;
        asciiArtPane.updateArtString("");
        fontField.setText("");
        charWidthField.setText("");

        toolBar.setDisable(true);
        borderPane.setCenter(new Label("No Media Selected."));
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
    public void fontHeightEntered(KeyEvent keyEvent) {
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
        assert asciiArtBuilder != null;

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
        if (newCharWidth > asciiArtBuilder.maxCharWidth()) {
            newCharWidth = asciiArtBuilder.maxCharWidth();
        }

        setAppCharWidth(newCharWidth);
    }

    @FXML
    public void invertedShadingClicked() {
        asciiArtBuilder.setInvertedShading(invertedShadingBtn.isSelected());
        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
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

    @FXML
    public void centerBtnPressed() {
    }

    @FXML
    public void centerBtnReleased() {
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

    /**
     * uses a FileChooser to select a file.
     * @param title FileChooser title property.
     * @param dir FileChooser initialDirectory property.
     * @param filter A single extension filter for fileChooser.
     * @return the user-selected file (null if cancelled).
     */
    private File selectFile(String title, File dir, FileChooser.ExtensionFilter filter) {
        return initFileChooser(title, dir, filter).showOpenDialog(new Stage());
    }

    private boolean saveAsTxt() {
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

    private boolean chooseVideoFile() {
        final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP4 Files", "*.mp4");
        File selected = selectFile("Select an MP4 File...", curChooseDir, filter);

        if (selected == null) return false;

        final boolean viewRequiresInit = asciiArtBuilder == null;
        VideoSource videoSource;
        try {
            videoSource = new VideoFileAdapter(selected);
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return false;
        }

        if (viewRequiresInit) {
            toolBar.setDisable(false);
            copyMenuItem.setDisable(false);
            saveMenuItem.setDisable(false);
            borderPane.setCenter(asciiArtPane);
            fontField.setText(String.valueOf(asciiArtPane.getFontSize()));
            charWidthField.setText("1");
        }

        asciiArtBuilder = new AsciiVideoBuilder(videoSource)
                .setInvertedShading(invertedShadingBtn.isSelected())
                .setCharWidth(Integer.parseInt(charWidthField.getText()));

        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
        charWidthField.setText(String.valueOf(asciiArtBuilder.getCharWidth()));
        curChooseDir = new File(selected.getParent());
        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
        ((Stage) borderPane.getScene().getWindow()).setTitle(asciiArtBuilder.getName() + " - " + App.TITLE);

        return true;

    }

    private boolean chooseImageFile() {
        final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg"
        );
        File selected = selectFile("Select an Image...", curChooseDir, filter);

        if (selected == null) return false;

        final boolean viewRequiresInit = asciiArtBuilder == null;
        ImageSource imageSource;
        try {
            imageSource = new ImageFileAdapter(selected);
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return false;
        }

        if (viewRequiresInit) {
            toolBar.setDisable(false);
            copyMenuItem.setDisable(false);
            saveMenuItem.setDisable(false);
            borderPane.setCenter(asciiArtPane);
            fontField.setText(String.valueOf(asciiArtPane.getFontSize()));
            charWidthField.setText("1");
        }

        asciiArtBuilder = new AsciiImageBuilder(imageSource)
                .setInvertedShading(invertedShadingBtn.isSelected())
                .setCharWidth(Integer.parseInt(charWidthField.getText()));

        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
        charWidthField.setText(String.valueOf(asciiArtBuilder.getCharWidth()));
        curChooseDir = new File(selected.getParent());
        asciiArtPane.updateArtString(asciiArtBuilder.getResult()[0]);
        ((Stage) borderPane.getScene().getWindow()).setTitle(asciiArtBuilder.getName() + " - " + App.TITLE);

        return true;
    }

}
