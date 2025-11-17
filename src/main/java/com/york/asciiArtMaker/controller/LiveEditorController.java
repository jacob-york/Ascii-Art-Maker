package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.DefaultCameraAdapter;
import com.york.asciiArtMaker.model.adapters.LiveSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiLiveBuilder;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.FPSTracker;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LiveEditorController extends AsciiEditorController {

    private Timeline playLoop;
    private FPSTracker fpsTracker;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Button togglePlayButton;
    private boolean userPaused;

    private AsciiImage curImage;

    @Override
    public void initialize() {
        super.initialize();
        fpsTracker = new FPSTracker();
        AnchorPane.setTopAnchor(fpsTracker, 105.0);
        AnchorPane.setRightAnchor(fpsTracker, 40.0);
        anchorPane.getChildren().add(fpsTracker);
        userPaused = false;
    }

    @Override
    AsciiImage getAsciiImageFrame() {
        return ((AsciiLiveBuilder) asciiArtBuilder).buildCurrentFrame();
    }

    public void setLiveSource(LiveSource liveSource) {
        asciiArtBuilder = new AsciiLiveBuilder(liveSource)
                .setCharWidth(INIT_CHAR_WIDTH)
                .setInvertedShading(activeColorTheme.invertedShading);
        fpsTracker.setTextFill(activeColorTheme.invertedShading ? Color.WHITE : Color.BLACK);


        double fps = liveSource.getFPS();
        playLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / fps), (ActionEvent event) -> {
            if (playLoop.getStatus() == Animation.Status.RUNNING) {
                asciiViewportPane.updateExistingContent(getAsciiImageFrame());
                fpsTracker.tick();
            }
        }));

        playLoop.setCycleCount(Timeline.INDEFINITE);
        charWidthField.setText(String.valueOf(INIT_CHAR_WIDTH));
        asciiViewportPane.setNewContent(getAsciiImageFrame());
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);

        playLoop.play();
    }

    @Override
    protected void configureAppCharWidth(int newCharWidth) {
        super.configureAppCharWidth(newCharWidth);
        if (playLoop != null && playLoop.getStatus() != Animation.Status.RUNNING) {
            asciiViewportPane.updateExistingContent(getAsciiImageFrame());
        }
    }

    @Override
    protected void configureAppInvertedShading(boolean newInvertedShading) {
        super.configureAppInvertedShading(newInvertedShading);
        if (playLoop != null && playLoop.getStatus() != Animation.Status.RUNNING) {
            asciiViewportPane.unsafeSetContent(getAsciiImageFrame());
        }
    }

    @Override
    public boolean newAsciiImageFromFileChosen() {
        if (!userPaused) pause();

        boolean fileChosen = super.newAsciiImageFromFileChosen();
        if (!fileChosen && !userPaused) play();

        return fileChosen;
    }

    @Override
    public boolean newAsciiVideoFromFileChosen() {
        if (!userPaused) pause();

        boolean fileChosen = super.newAsciiVideoFromFileChosen();
        if (!fileChosen && !userPaused) play();

        return fileChosen;
    }


    @Override
    public void liveRenderFromCameraChosen() {
        closeSource();
        AsciiArtMaker.tryLaunchView("fxml/webcam_loading_dialog.fxml").ifPresent(loadingDialogController -> {
            Task<LiveSource> task = new Task<>() {
                @Override
                protected LiveSource call() {
                    return new DefaultCameraAdapter();
                }
            };

            task.setOnSucceeded(event -> {
                ((WebcamLoadingDialogController) loadingDialogController).stage.close();
                setLiveSource(task.getValue());
            });

            new Thread(task).start();
        });
    }

    private void closeSource() {
        playLoop.stop();
        ((AsciiLiveBuilder) asciiArtBuilder).close();
    }

    private void setPlayButtonIcon(String name) {
        ImageView imageView = new ImageView(
                new Image(AsciiArtMaker.getResourcePath("icons/" + name + ".png")));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        togglePlayButton.setGraphic(imageView);
    }

    public void play() {
        playLoop.play();
        ((AsciiLiveBuilder) asciiArtBuilder).getLiveSource().unpause();
        setPlayButtonIcon("pause");
    }

    public void pause() {
        playLoop.pause();
        ((AsciiLiveBuilder) asciiArtBuilder).getLiveSource().pause();
        setPlayButtonIcon("play");
    }

    public void togglePlayAction() {
        switch (playLoop.getStatus()) {
            case PAUSED -> {
                play();
                userPaused = false;
            }
            case RUNNING -> {
                pause();
                userPaused = true;
            }
        }
    }

    @Override
    public void bgColorSelected() {
        super.bgColorSelected();
        if (fpsTracker != null) fpsTracker.setTextFill(bgColorPicker.getValue().invert());
    }

    @Override
    protected void styleView(ColorTheme colorTheme) {
        super.styleView(colorTheme);
        if (fpsTracker != null) fpsTracker.setTextFill(colorTheme.bgColor.invert());
    }

    @Override
    public void close() {
        closeSource();
        super.close();
    }
}
