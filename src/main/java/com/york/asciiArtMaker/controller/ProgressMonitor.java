package com.york.asciiArtMaker.controller;

public interface ProgressMonitor extends Cancellable {
    void setProgress(double progress);
    void finish();
}
