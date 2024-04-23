package com.york.asciiartstudio.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;

public class LoadingDialog extends Alert {
    private ProgressBar progressBar;
    public LoadingDialog(AlertType alertType) {
        super(alertType);
    }
}
