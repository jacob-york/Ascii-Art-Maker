package com.york.asciiArtMaker;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MultipleStagesExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the primary stage
        primaryStage.setTitle("Primary Stage");

        Button primaryButton = new Button("Open Secondary Stage");
        primaryButton.setOnAction(e -> {
            openSecondaryStage();
        });

        StackPane primaryLayout = new StackPane(primaryButton);
        Scene primaryScene = new Scene(primaryLayout, 300, 250);
        primaryStage.setScene(primaryScene);

        primaryStage.show();
    }

    private void openSecondaryStage() {
        // Create the secondary stage
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Secondary Stage");

        Button secondaryButton = new Button("Close Secondary Stage");
        secondaryButton.setOnAction(e -> {
            secondaryStage.close();
        });

        StackPane secondaryLayout = new StackPane(secondaryButton);
        Scene secondaryScene = new Scene(secondaryLayout, 300, 250);
        secondaryStage.setScene(secondaryScene);

        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
