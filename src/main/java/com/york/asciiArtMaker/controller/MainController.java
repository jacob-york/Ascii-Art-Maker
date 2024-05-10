package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.adapters.*;
import com.york.asciiArtMaker.asciiArt.AsciiImageBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.models.AppModel;
import com.york.asciiArtMaker.models.ImageModel;
import com.york.asciiArtMaker.models.NullModel;
import com.york.asciiArtMaker.models.VideoModel;
import com.york.asciiArtMaker.view.AsciiArtPane;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.ThemeMenuItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

// TODO: This is bloated.
public class MainController implements VFCSObserver {

    @FXML
    public VBox zoomControls;
    @FXML
    public VBox charWidthControls;
    @FXML
    public HBox shadingControls;
    @FXML
    public VBox bGColorControls;
    @FXML
    public VBox textColorControls;
    @FXML
    public VBox videoPlayerControls;
    @FXML
    public HBox fileSaveControls;

    @FXML
    public BorderPane borderPane;
    @FXML
    public ToolBar toolBar;
    @FXML
    private MenuButton colorThemeMenu;
    @FXML
    public Button zoomInBtn;
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
    public MenuItem openFile;
    @FXML
    public HBox videoControlBox;
    @FXML
    public Button playVideoBtn;
    @FXML
    public Button prevFrameBtn;
    @FXML
    public Button nextFrameBtn;
    @FXML
    public MenuItem exportTxtMenuItem;
    @FXML
    public MenuItem saveImageMenuItem;
    @FXML
    public MenuItem compileFramesMenuItem;
    @FXML
    public MenuItem copyMenuItem;
    @FXML
    public Button saveAsMp4Btn;

    @FXML
    public Button exportTxtBtn;
    @FXML
    public Button saveImageBtn;


    private AsciiArtPane asciiArtPane;
    private AppModel model;
    private LongClickManager longClickManager;
    private FileManager fileManager;


    @FXML
    public void initialize() {
        fileManager = AsciiArtMaker.getFileManager();
        asciiArtPane = new AsciiArtPane();
        model = new NullModel();
        fontField.setText("");
        charWidthField.setText("");

        longClickManager = new LongClickManager();

        longClickManager.addRunnable("zoomIn", () -> setAppFontHeight(Double.parseDouble(fontField.getText()) + 1));
        longClickManager.addRunnable("zoomOut", () -> setAppFontHeight(Double.parseDouble(fontField.getText()) - 1));
        longClickManager.addRunnable("charWidthUp", () -> setAppCharWidth(model.getCharWidth() + 1));
        longClickManager.addRunnable("charWidthDown", () -> setAppCharWidth(model.getCharWidth() - 1));
        longClickManager.addRunnable("prevFrameBtn", () -> model.prevFrame());
        longClickManager.addRunnable("nextFrameBtn", () -> model.nextFrame());

        for (ColorTheme colorTheme : ColorTheme.values()) {
            colorThemeMenu.getItems().add(new ThemeMenuItem(colorTheme, () -> applyTheme(colorTheme)));
        }
    }

    @FXML
    public void zoomInPressed() {
        longClickManager.start("zoomIn");
    }

    @FXML
    public void zoomInReleased() {
        longClickManager.stop("zoomIn");
    }

    @FXML
    public void zoomOutPressed() {
        longClickManager.start("zoomOut");
    }

    @FXML
    public void zoomOutReleased() {
        longClickManager.stop("zoomOut");
    }

    @FXML
    public void charWidthUpPressed() {
        longClickManager.start("charWidthUp");
    }

    @FXML
    public void charWidthUpReleased() {
        longClickManager.stop("charWidthUp");
    }

    @FXML
    public void charWidthDownPressed() {
        longClickManager.start("charWidthDown");
    }

    @FXML
    public void charWidthDownReleased() {
        longClickManager.stop("charWidthDown");
    }

    @FXML
    public void openItemClicked() {
        openFile();
    }

    @FXML
    public void compileFramesClicked() {
        String oldTitle = getTitle();
        setTitle("loading...");
        compileFrames();
        setTitle(oldTitle);
    }

    @FXML
    public void webcamMenuItemClicked() {

    }

    @FXML
    public void exportTxtMenuItemClicked() {
        model.pauseVideoPlayer();

        try {
            fileManager.saveTxtFile(model.getCurFrame());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void saveImageMenuItemClicked() {
        model.pauseVideoPlayer();

        ImageRenderer imageRenderer = new ImageRenderer(asciiArtPane.getFontSize(),
                asciiArtPane.getTextAreaWidth(), asciiArtPane.getTextAreaHeight(), AsciiArtPane.getDefaultFont())
                .setBgColor(bgColorPicker.getValue())
                .setTextColor(textColorPicker.getValue());

        try {
            fileManager.saveImage(model.getCurFrame(), imageRenderer);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void saveAsMp4BtnClicked() {
        model.pauseVideoPlayer();
        if (model instanceof VideoModel videoModel) {

            ImageRenderer imageRender = new ImageRenderer(asciiArtPane.getFontSize(),
                    asciiArtPane.getTextAreaWidth(), asciiArtPane.getTextAreaHeight(), AsciiArtPane.getDefaultFont())
                    .setBgColor(bgColorPicker.getValue())
                    .setTextColor(textColorPicker.getValue())
                    .setImageType(BufferedImage.TYPE_3BYTE_BGR);;

            try {
                fileManager.saveVideo(videoModel.getCompiledArt(), imageRender);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    public void clearMenuItemClicked() {
        model.close();
        model = new NullModel();
        model.configureGUI(this);
    }

    @FXML
    public void exitMenuItemClicked() {
        Platform.exit();
    }

    @FXML
    public void copyMenuItemClicked() {
        model.pauseVideoPlayer();
        AsciiArtMaker.copyToClipboard(asciiArtPane.getCurText());
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
        copyMenuItemClicked();
    }

    @FXML
    public void saveBtnPressed() {
        saveImageMenuItemClicked();
    }

    @FXML
    public void prevFramePressed() {
        longClickManager.start("prevFrameBtn");
    }

    @FXML
    public void prevFrameReleased() {
        longClickManager.stop("prevFrameBtn");
    }

    @FXML
    public void playBtnPressed() {
        model.toggleVideoPlayerState();
    }

    @FXML
    public void nextFramePressed() {
        longClickManager.start("nextFrameBtn");
    }

    @FXML
    public void nextFrameReleased() {
        longClickManager.stop("nextFrameBtn");
    }

    @FXML
    public void saveImageBtnClicked() {
        saveImageMenuItemClicked();
    }

    @FXML
    public void exportTxtBtnClicked() {
        exportTxtMenuItemClicked();
    }

    public String getTitle() {
        return ((Stage) borderPane.getScene().getWindow())
                .getTitle();
    }

    public void setTitle(String newTitle) {
        ((Stage) borderPane.getScene().getWindow())
                .setTitle(newTitle);
    }

    private void compileFrames() {
        if (model instanceof VideoModel videoModel) {
            videoModel.getCompiledArt();
        }
    }

    public AsciiArtPane getAsciiArtPane() {
        return asciiArtPane;
    }

    public void fontFromField() {
        double newVal;
        try {
            newVal = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        setAppFontHeight(AppModel.ensureInRange(newVal, 1, Integer.MAX_VALUE));
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

    public boolean setAppFontHeight(double newFontHeight) {
        if (!asciiArtPane.setFontSize(newFontHeight)) return false;

        fontField.setText(String.valueOf(newFontHeight));
        return true;
    }

    public boolean setAppCharWidth(int newCharWidth) {
        final double newFontHeight = model.calcNewFontHeight(newCharWidth, Double.parseDouble(fontField.getText()));
        if (!setAppFontHeight(newFontHeight)) return false;

        int adjustedCW = model.setCharWidth(newCharWidth);

        charWidthField.setText(String.valueOf(adjustedCW));
        asciiArtPane.setText(model.getCurFrame());
        return true;
    }

    public void setAppInvertedShading() {
        model.setInvertedShading(invertedShadingBtn.isSelected());
        asciiArtPane.setText(model.getCurFrame());
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
        asciiArtPane.setText(model.getCurFrame());
        invertedShadingBtn.setSelected(invertedShading);
    }

    public boolean openFile() {
        Optional<File> maybeFile = fileManager.selectFile();
        if (maybeFile.isEmpty()) return false;
        File selected = maybeFile.get();

        if (FileManager.isVideoFile(selected)) {
            return beginLoading(selected);
        } else if (FileManager.isImageFile(selected)) {
            return loadImageModel(selected);
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid file type.").showAndWait();
            return false;
        }
    }

    private boolean beginLoading(File selected) {
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/video_loading_dialog.fxml"));
        Parent root;
        Stage loadDialogStage;
        try {
            loadDialogStage = new Stage();
            root = loader.load();
            Scene loadDialogScene = new Scene(root);
            loadDialogStage.setScene(loadDialogScene);
            loadDialogStage.initOwner(AsciiArtMaker.getMainStage());
        } catch (IOException e) {
            return false;
        }

        VideoFileConnectionService vfcs = new VideoFileConnectionService(selected);

        LoadingDialogController ldController = loader.getController();
        ldController.addObserver(vfcs);
        ldController.setDisplayText("Gathering frame data");

        vfcs.addObserver(this);
        vfcs.addObserver(ldController);
        vfcs.start();
        loadDialogStage.show();

        return true;
    }

    private boolean loadImageModel(File selected) {
        ImageSource imageSource = new ImageFileAdapter(selected);
        AsciiImageBuilder aib = new AsciiImageBuilder(imageSource);

        model.close();
        model = new ImageModel(aib);
        model.configureGUI(this);

        return true;
    }

    @Override
    public void setTotalFrames(int totalFrames) {}

    @Override
    public void userCancel() {
        String errorMsg = "There was an error opening your video file.";

        new Alert(Alert.AlertType.ERROR, errorMsg).showAndWait();
    }

    @Override
    public void setCurFrame(int curFrame) {}

    @Override
    public void success(VideoSource videoSource) {
        model.close();

        AsciiVideoBuilder avb = new AsciiVideoBuilder(videoSource);
        model = new VideoModel(avb, playVideoBtn, asciiArtPane);
        model.configureGUI(this);
    }
}
