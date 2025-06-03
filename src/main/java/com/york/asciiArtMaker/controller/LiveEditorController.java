package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.DefaultCameraAdapter;
import com.york.asciiArtMaker.model.adapters.LiveSource;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiLiveBuilder;
import com.york.asciiArtMaker.view.ColorTheme;
import com.york.asciiArtMaker.view.FPSTracker;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LiveEditorController extends AsciiEditorController {

    private Timeline playLoop;
    private FPSTracker fpsTracker;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Button togglePlayButton;

    @Override
    public void initialize() {
        super.initialize();
        fpsTracker = new FPSTracker();
        AnchorPane.setTopAnchor(fpsTracker, 105.0);
        AnchorPane.setRightAnchor(fpsTracker, 40.0);
        anchorPane.getChildren().add(fpsTracker);
    }

    @Override
    AsciiImage getAsciiImageFrame() {
        return ((AsciiLiveBuilder) asciiArtBuilder).buildCurrentFrame();
    }

    public void setLiveSource(LiveSource liveSource) {
        asciiArtBuilder = new AsciiLiveBuilder(liveSource)
                .setCharWidth(INIT_CHAR_WIDTH)
                .setInvertedShading(activeColorTheme.invertedShading);

        int fps = 30;
        playLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / fps), (ActionEvent event) -> {
            asciiViewportPane.unsafeSetContent(getAsciiImageFrame());
            fpsTracker.tick();
        }));

        playLoop.setCycleCount(Timeline.INDEFINITE);
        charWidthField.setText(String.valueOf(INIT_CHAR_WIDTH));
        asciiViewportPane.setNewContent(getAsciiImageFrame());
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);

        playLoop.play();
    }

    @Override
    protected void configureAppCharWidth(int newCharWidth)  {
        if (playLoop != null) playLoop.pause();
        super.configureAppCharWidth(newCharWidth);
        if (playLoop != null) playLoop.play();
    }

    @Override
    public void invertedShadingClicked() {
        if (playLoop != null) playLoop.pause();
        super.invertedShadingClicked();
        if (playLoop != null) playLoop.play();
    }

    @Override
    public void applyTheme(ColorTheme colorTheme) {
        if (playLoop != null) playLoop.pause();
        super.applyTheme(colorTheme);
        if (playLoop != null) playLoop.play();
    }

    @Override
    public boolean newAsciiImageFromFileChosen() {
        if(super.newAsciiImageFromFileChosen()) {
            closeSource();
            return true;
        } else return false;
    }

    @Override
    public void liveRenderFromCameraChosen() {
        closeSource();
        setLiveSource(new DefaultCameraAdapter());
    }

    @Override
    public void acceptResult(VideoSource result) {
        super.acceptResult(result);
        closeSource();
    }

    @Override
    public void closeEditorAction() {
        closeSource();

        ((Stage) borderPane.getScene().getWindow()).close();
        AsciiArtMaker.launchHome();
    }

    @Override
    public void quitProject() {
        closeSource();

        System.exit(0);
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
        setPlayButtonIcon("pause");
    }

    public void pause() {
        playLoop.pause();
        setPlayButtonIcon("play");
    }

    public void togglePlayAction() {
        switch (playLoop.getStatus()) {
            case PAUSED -> play();
            case RUNNING -> pause();
        }
    }
}
