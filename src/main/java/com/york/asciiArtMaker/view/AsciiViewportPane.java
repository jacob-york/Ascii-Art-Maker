package com.york.asciiArtMaker.view;

import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import javafx.scene.Group;

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
    public static final double MAX_CHAR_RENDER_HEIGHT = 75;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private AsciiImage art;
    private double virtualContentXPos;
    private double virtualContentYPos;
    private double virtualContentZoom;
    private double zeroedScale;
    private double zeroedWidth;
    private double zeroedHeight;
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

        virtualContentXPos = 0.0;
        virtualContentYPos = 0.0;
        virtualContentZoom = 1.0;

        text = new Text();
        text.setFont(new Font(getDefaultFontName(), 13));
        textGroup = new Group(text);
        setPrefWidth(prefWidth);
        setPrefHeight(prefHeight);
        clip = new Rectangle(prefWidth, prefHeight);
        setClip(clip);

        widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.doubleValue());
            refreshContentXPos();
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(newVal.doubleValue());
            refreshContentYPos();
        });

        refreshContentPos();
        getChildren().add(textGroup);
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
        centerContentPos();
        this.art = newContent;
    }

    public boolean updateExistingContent(AsciiImage newContent) {
        text.setText(newContent.toStr());
        final double charWidthScalar = ((double) newContent.charWidth() / (double) art.charWidth());
        zeroedScale *= charWidthScalar;
        setContentScale(getContentScale() * charWidthScalar);
        refreshContentPos();

        this.art = newContent;
        return true;
    }

    /**
     * <p>ONLY updates the nested text attribute itself; No rescaling, no re-centering, no repositioning, etc.</p>
     * <p>The intended use case for this is when you're confident that the new art will have the EXACT same dimensions as
     * the previous art, and you'd like to skip some unnecessary computation.</p>
     * @param newContent the new Art
     */
    public void unsafeSetContent(AsciiImage newContent) {
        text.setText(newContent.toStr());
        this.art = newContent;
    }

    public void centerContentPos() {
        setContentScale(1);
        zeroedScale = (getPrefHeight() * (2.0/3.0)) / getPhysicalContentHeight();
        zeroedWidth = getContentWidth();
        zeroedHeight = getContentHeight();

        boolean windowIsShowing = getScene().getWindow().isShowing();
        double desiredContentHeight = (windowIsShowing ? getHeight() : getPrefHeight()) * (2.0/3.0);
        double appliedScale = desiredContentHeight / getPhysicalContentHeight();
        setContentScale(appliedScale);
        virtualContentZoom = appliedScale / zeroedScale;

        virtualContentXPos = 0;
        virtualContentYPos = 0;
        refreshContentPos();
    }

    private void refreshContentPos() {
        refreshContentXPos();
        refreshContentYPos();
    }

    private void refreshContentXPos() {
        textGroup.setTranslateX(getWidth()/2.0 - (getPhysicalContentWidth()/2.0) + virtualContentXPos);
    }

    private void refreshContentYPos() {
        textGroup.setTranslateY(getHeight()/2.0 - (getPhysicalContentHeight()/2.0) + virtualContentYPos);
    }

    public double getContentZoom() {
        return virtualContentZoom;
    }
    public void setContentZoom(double newZoom) {
        double newScale = zeroedScale * newZoom;

        if (virtualContentZoom - newZoom > 0 && newZoom > 0) {
            virtualContentZoom = newZoom;
            setContentScale(newScale);
        } else if (virtualContentZoom - newZoom < 0 && ((zeroedHeight*newZoom) / art.height()) <= MAX_CHAR_RENDER_HEIGHT) {
            virtualContentZoom = newZoom;
            setContentScale(newScale);
        }

        virtualContentXPos = ensureVirtualXInRange(virtualContentXPos);
        virtualContentYPos = ensureVirtualYInRange(virtualContentYPos);
        refreshContentPos();
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
        minX = -1*getWidth()/2.0 - (getContentWidth()/2.0) + VIEWPORT_PADDING_PIXELS;
        maxX = getWidth()/2.0 + (getContentWidth()/2.0) - VIEWPORT_PADDING_PIXELS;
    }
    private void recalibrateYRange() {
        minY = -1*getHeight()/2.0 - (getContentHeight()/2.0) + VIEWPORT_PADDING_PIXELS;
        maxY = getHeight()/2.0 + (getContentHeight()/2.0) - VIEWPORT_PADDING_PIXELS;
    }

    public double getContentXPos() {
        return virtualContentXPos;
    }
    public void setContentXPos(double newXPos) {
        virtualContentXPos = ensureVirtualXInRange(newXPos);
        refreshContentXPos();
    }

    public double getContentYPos() {
        return virtualContentYPos;
    }
    public void setContentYPos(double newYPos) {
        virtualContentYPos = ensureVirtualYInRange(newYPos);
        refreshContentYPos();
    }

    private double ensureVirtualXInRange(double newXPos) {
        return newXPos < minX ? minX : (newXPos > maxX ? maxX : newXPos);
    }
    private double ensureVirtualYInRange(double newYPos) {
        return newYPos < minY ? minY : (newYPos > maxY ? maxY : newYPos);
    }

    // virtual
    public double getContentWidth() {
        return getPhysicalContentWidth() * textGroup.getScaleX();
    }

    // virtual
    public double getContentHeight() {
        return getPhysicalContentHeight() * textGroup.getScaleY();
    }

    private double getPhysicalContentWidth() {
        return textGroup.getBoundsInLocal().getWidth();
    }

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
