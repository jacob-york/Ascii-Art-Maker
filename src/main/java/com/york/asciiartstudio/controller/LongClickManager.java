package com.york.asciiartstudio.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for Timeliness, each with a unique string id, that are ran in a loop which can be toggled on or off
 * with start(id) or stop(id).
 */
public class LongClickManager {

    private static final int LONG_PRESS = 2;

    private final Map<String, Timeline> timelines;
    private final Map<String, Runnable> runnables;

    private int pressDuration;

    public LongClickManager() {
        this.timelines = new HashMap<>();
        this.runnables = new HashMap<>();
        pressDuration = 0;
    }

    public boolean addRunnable(String id, Runnable runnable) {
        if (timelines.containsKey(id)) return false;
        runnables.put(id, runnable);
        timelines.put(id, initTimeline(runnable));

        return true;
    }

    public boolean removeRunnable(String id) {
        if (!timelines.containsKey(id)) return false;
        runnables.remove(id);
        timelines.remove(id);

        return true;
    }

    public boolean start(String id) {
        if (!timelines.containsKey(id)) return false;
        runnables.get(id).run();
        timelines.get(id).play();

        return true;
    }

    public boolean stop(String id) {
        if (!timelines.containsKey(id)) return false;

        timelines.get(id).stop();
        pressDuration = 0;

        return true;
    }

    public Timeline initTimeline(Runnable runnable) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.15), (ActionEvent event) -> {

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
}
