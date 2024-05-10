package com.york.asciiArtMaker.adapters;

public interface VFCSObserver {
    void setTotalFrames(int totalFrames);
    void userCancel();
    void setCurFrame(int curFrame);
    void success(VideoSource videoSource);
}
