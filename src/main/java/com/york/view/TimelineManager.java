package com.york.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class TimelineManager {

    private static final Duration DURATION = Duration.seconds(.1);

    private static final int LONG_PRESS = 3;  // time of long press = DURATION x LONG_PRESS

    private final Timeline zoomInTimeline;

    private final Timeline zoomOutTimeline;

    private final Timeline charWidthInTimeline;

    private final Timeline charWidthOutTimeline;

    private final Runnable zoomIn;

    private final Runnable zoomOut;

    private final Runnable charWidthUp;

    private final Runnable charWidthDown;

    private int pressDuration;

    public TimelineManager(Runnable zoomIn, Runnable zoomOut,
                           Runnable charWidthUp, Runnable charWidthDown
    ) {
        this.zoomIn = zoomIn;
        this.zoomOut = zoomOut;
        this.charWidthUp = charWidthUp;
        this.charWidthDown = charWidthDown;

        pressDuration = 0;

        zoomInTimeline = initTimeline(zoomIn);
        zoomOutTimeline = initTimeline(zoomOut);
        charWidthInTimeline = initTimeline(charWidthUp);
        charWidthOutTimeline = initTimeline(charWidthDown);
    }

    private Timeline initTimeline(Runnable runnable) {
        Timeline timeline = new Timeline(new KeyFrame(DURATION, (ActionEvent event) -> {

            if (pressDuration > LONG_PRESS) {
                runnable.run();
            }
            else {
                pressDuration++;
            }

        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        return timeline;
    }

    public void zoomIn() {
        zoomIn.run();
        zoomInTimeline.play();
    }

    public void stopZoom() {
        zoomInTimeline.stop();
        zoomOutTimeline.stop();
        pressDuration = 0;
    }

    public void zoomOut() {
        zoomOut.run();
        zoomOutTimeline.play();
    }

    public void charWidthUp() {
        charWidthUp.run();
        charWidthInTimeline.play();
    }

    public void stopCharWidth() {
        charWidthInTimeline.stop();
        charWidthOutTimeline.stop();
        pressDuration = 0;
    }

    public void charWidthDown() {
        charWidthDown.run();
        charWidthOutTimeline.play();
    }
}
