package com.york.asciiArtMaker.observer;

import javafx.scene.control.Dialog;

public class LoadingDialog extends Dialog<Boolean> {


    public LoadingDialog() {
        super();
        setTitle("generating frame stubs...\nCurrent Frame: 0");
    }

    public void update(Integer curFrame) {
        setTitle("generating frame stubs...\nCurrent Frame: " + curFrame);
    }
}
