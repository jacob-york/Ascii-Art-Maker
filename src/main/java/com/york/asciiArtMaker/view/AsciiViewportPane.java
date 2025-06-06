package com.york.asciiArtMaker.view;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import javafx.scene.Cursor;
import javafx.scene.Group;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Pane in which ascii art is displayed; the Core of this Project's UI.
 */
public class AsciiViewportPane extends Pane {

    public static final double VIEWPORT_PADDING_PIXELS = 50;
    public static final double MAX_CHAR_RENDER_HEIGHT = 40;
    public static final double MIN_CHAR_RENDER_HEIGHT = 1;
    public static final double ZOOM_TRANSFORM = 0.25;
    public static final double PANNING_TRANSFORM = 50;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double mouseXDragSpace;
    private double mouseYDragSpace;

    private AsciiArtBuilder asciiArtBuilder;
    private AsciiImage art;
    private double virtualContentXPos;
    private double virtualContentYPos;
    private double virtualContentZoom;
    private double baselineScale;
    private final double DEFAULT_HEIGHT;
    private final Rectangle clip;
    private final Text text;
    private final Group textGroup;

    public AsciiViewportPane(double prefWidth, double prefHeight) {
        super();

        art = null;

        minX = prefWidth / 2.0;
        maxX = prefWidth / 2.0;
        minY = prefHeight / 2.0;
        maxY = prefHeight / 2.0;

        DEFAULT_HEIGHT = prefHeight * 0.80;

        virtualContentXPos = 0.0;
        virtualContentYPos = 0.0;
        virtualContentZoom = 1.0;

        text = new Text();
        text.setFont(new Font(getDefaultFontName(), 13));
        textGroup = new Group(text);

        setPrefSize(prefWidth, prefHeight);
        clip = new Rectangle(prefWidth, prefHeight);
        setClip(clip);

        widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.doubleValue());
            updateContentXPos();
            recalibrateXRange();
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(newVal.doubleValue());
            updateContentYPos();
            recalibrateYRange();
        });

        updateContentPos();
        initializeMouseControls();
        getChildren().add(textGroup);
    }

    private void initializeMouseControls() {
        setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                setCursor(Cursor.CLOSED_HAND);
                mouseXDragSpace = mouseEvent.getSceneX() - getContentXPos();
                mouseYDragSpace = mouseEvent.getSceneY() - getContentYPos();
            }
        });
        setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                setContentXPos(mouseEvent.getSceneX() - mouseXDragSpace);
                setContentYPos(mouseEvent.getSceneY() - mouseYDragSpace);
            }
        });
        setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
                setCursor(Cursor.OPEN_HAND);
        });
    }

    public double getViewportWidth() {
        return windowIsShowing() ? getWidth() : getPrefWidth();
    }

    public double getViewportHeight() {
        return windowIsShowing() ? getHeight() : getPrefHeight();
    }

    public boolean hasContent() {
        return art != null;
    }

    public static String getDefaultFontName() {
        String os = System.getProperty("os.menuName", "generic").toLowerCase();
        if (os.contains("win")) {
            return "Consolas";
        } else if (os.contains("mac") || os.contains("darwin")) {
            return "SF Mono";
        } else if (os.contains("linux")) {
            return "Ubuntu Mono Regular";
        } else {
            return "Consolas";
        }
    }

    public String getContentAsString() {
        return text.getText();
    }

    public void setNewContent(AsciiImage newContent) {
        text.setText(newContent.toStr());
        this.art = newContent;

        // recalculate baseline scale
        baselineScale = DEFAULT_HEIGHT / getPhysicalContentHeight();

        setContentScale(baselineScale);
        virtualContentZoom = 1;
        setContentXPos(0);
        setContentYPos(0);
    }

    /**
     * @param newContent
     * @return false if any resizing needed to be done to keep the art size in the valid char render height range, otherwise true
     */
    public boolean updateExistingContent(AsciiImage newContent) {
        if (newContent.charWidth() == art.charWidth()) {
            unsafeSetContent(newContent);
            return false;

        } else {
            text.setText(newContent.toStr());

            final double charWidthScalar = ((double) newContent.charWidth() / (double) art.charWidth());
            double newScale = getContentScale() * charWidthScalar;
            baselineScale *= charWidthScalar;

            double newCharRenderHeight = getPhysicalContentHeight()*newScale / newContent.height();
            double adjustedCharRenderHeight = AsciiArtMaker.ensureInRange(
                    newCharRenderHeight, MIN_CHAR_RENDER_HEIGHT, MAX_CHAR_RENDER_HEIGHT);

            // compute adjusted scale and adjusted virtualZoom
            if (adjustedCharRenderHeight != newCharRenderHeight) {
                newScale = adjustedCharRenderHeight*newContent.height() / getPhysicalContentHeight();
                virtualContentZoom = newScale / baselineScale;
            }

            setContentScale(newScale);
            updateContentPos();
            this.art = newContent;
            return adjustedCharRenderHeight == newCharRenderHeight;
        }
    }

    private boolean windowIsShowing() {
        return getScene() != null && getScene().getWindow().isShowing();
    }

    /**
     * <p>ONLY updates the nested hText attribute itself; No rescaling, no re-centering, no repositioning, etc.</p>
     * <p>The intended use case for this is when you're confident that the new art will have the EXACT same dimensions as
     * the previous art, and you'd like to skip some unnecessary computation.</p>
     * @param newContent the new Art
     */
    public void unsafeSetContent(AsciiImage newContent) {
        text.setText(newContent.toStr());
        this.art = newContent;
    }

    public void fitContentToArea() {
        double newScale = getViewportHeight() / getPhysicalContentHeight();
        setContentZoom(newScale / baselineScale);
        setContentXPos(0);
        setContentYPos(0);
    }

    private void updateContentPos() {
        updateContentXPos();
        updateContentYPos();
    }
    private void updateContentXPos() {
        virtualContentXPos = AsciiArtMaker.ensureInRange(virtualContentXPos, minX, maxX);
        textGroup.setTranslateX(getViewportWidth()/2.0 - (getPhysicalContentWidth()/2.0) + virtualContentXPos);
    }
    private void updateContentYPos() {
        virtualContentYPos = AsciiArtMaker.ensureInRange(virtualContentYPos, minY, maxY);
        textGroup.setTranslateY(getViewportHeight()/2.0 - (getPhysicalContentHeight()/2.0) + virtualContentYPos + 10);
    }

    public double getContentZoom() {
        return virtualContentZoom;
    }
    public boolean setContentZoom(double newZoom) {
        double adjustedZoom = ensureZoomInRange(newZoom);
        double newScale = baselineScale * adjustedZoom;
        virtualContentZoom = adjustedZoom;
        setContentScale(newScale);

        virtualContentXPos = AsciiArtMaker.ensureInRange(virtualContentXPos, minX, maxX);
        virtualContentYPos = AsciiArtMaker.ensureInRange(virtualContentYPos, minY, maxY);
        updateContentPos();

        return adjustedZoom == newZoom;
    }
    private double ensureZoomInRange(double newZoom) {
        double charRenderHeight = getPhysicalContentHeight()* baselineScale *newZoom / art.height();
        double adjustedCharRenderHeight = AsciiArtMaker.ensureInRange(
                charRenderHeight, MIN_CHAR_RENDER_HEIGHT, MAX_CHAR_RENDER_HEIGHT);

        if (adjustedCharRenderHeight == charRenderHeight) return newZoom;
        else return adjustedCharRenderHeight*art.height() / (getPhysicalContentHeight()* baselineScale);
    }

    private double getContentScale() {
        return textGroup.getScaleX();
    }
    private void setContentScale(double newScale) {
        textGroup.setScaleX(newScale);
        textGroup.setScaleY(newScale);
        recalibrateXRange();
        recalibrateYRange();
    }

    private void recalibrateXRange() {
        minX = -1*getViewportWidth()/2.0 - (getContentWidth()/2.0) + VIEWPORT_PADDING_PIXELS;
        maxX = getViewportWidth()/2.0 + (getContentWidth()/2.0) - VIEWPORT_PADDING_PIXELS;
    }
    private void recalibrateYRange() {
        minY = -1*getViewportHeight()/2.0 - (getContentHeight()/2.0) + VIEWPORT_PADDING_PIXELS;
        maxY = getViewportHeight()/2.0 + (getContentHeight()/2.0) - VIEWPORT_PADDING_PIXELS;
    }

    public double getContentXPos() {
        return virtualContentXPos;
    }

    /**
     * @param newXPos new x pos of the content, where the origin is the center of the viewport.
     * @return false if newXPos is out of bounds, otherwise true (if the value is out of bounds,
     * then the content will be placed at the min or max respectively).
     */
    public boolean setContentXPos(double newXPos) {
        double adjustedXPos = AsciiArtMaker.ensureInRange(newXPos, minX, maxX);
        virtualContentXPos = adjustedXPos;
        updateContentXPos();

        return adjustedXPos == newXPos;
    }

    public double getContentYPos() {
        return virtualContentYPos;
    }

    /**
     * @param newYPos new y pos of the content, where the origin is the center of the viewport.
     * @return false if newYPos is out of bounds, otherwise true (if the value is out of bounds,
     * then the content will be placed at the min or max respectively).
     */
    public boolean setContentYPos(double newYPos) {
        double adjustedYPos = AsciiArtMaker.ensureInRange(newYPos, minY, maxY);
        virtualContentYPos = adjustedYPos;
        updateContentYPos();

        return adjustedYPos == newYPos;
    }

    // virtual
    public double getContentWidth() {
        return textGroup.getBoundsInParent().getWidth();
    }

    // virtual
    public double getContentHeight() {
        return textGroup.getBoundsInParent().getHeight();
    }

    // physical
    private double getPhysicalContentWidth() {
        return textGroup.getBoundsInLocal().getWidth();
    }

    // physical
    private double getPhysicalContentHeight() {
        return textGroup.getBoundsInLocal().getHeight();
    }

    public void setBackgroundColor(Color value) {
        String hexCode = value.toString().substring(2, 8);
        setStyle("-fx-background-color: #" + hexCode + ";");
    }

    public void setTextColor(Color value) {
        text.setFill(value);
    }

}
