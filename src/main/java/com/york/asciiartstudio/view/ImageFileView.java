package com.york.asciiartstudio.view;

import com.york.asciiartstudio.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ImageFileView extends AnchorPane {

    public ImageFileView() {
        super();
        try {
            Parent root = new FXMLLoader(App.class.getResource("fxml/imageFileMode.fxml")).load();
            AnchorPane.setLeftAnchor(root, .0);
            AnchorPane.setRightAnchor(root, .0);
            AnchorPane.setTopAnchor(root, .0);
            AnchorPane.setBottomAnchor(root, .0);
            getChildren().add(root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
