package com.example;
	
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.example.asciiArt.AsciiImage;
import com.example.asciiArt.ImageLoader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;


public class GUI extends Application {
	
	private String curImagePath;
	
	private Label charWidthLabel;
	private TextField charWidthField;
	private Label invShadingLabel;
	private TextField invShadingField;
	private Label paletteLabel;
	private TextField paletteField;
	private Button render;
	private Button chooseImage;
	
	@Override
	public void start(Stage stage) {
		
		final String SRC_PATH = "D:\\Programming\\Projects\\JavaProjects\\Personal\\AsciiArtMaker\\src";
		Color bgColor = new Color(255, 255, 255);
		Image icon = new Image(SRC_PATH + "\\resources\\Images\\icon.png");
	
		curImagePath = null;
		
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		
		Scene appScene = new Scene(gridPane);
		
		charWidthLabel = new Label("Character Width:");
		charWidthField = new TextField();
		invShadingLabel = new Label("Invert Shading:");
		invShadingField = new TextField();
		paletteLabel = new Label("Palette:");
		paletteField = new TextField();
		
		charWidthField.setPrefColumnCount(2);
		charWidthField.setEditable(true);
		charWidthField.setText("1");
		
		invShadingField.setPrefColumnCount(1);
		invShadingField.setEditable(true);
		invShadingField.setText("0");
		
		paletteField.setPrefColumnCount(16);
		paletteField.setEditable(true);
		paletteField.setText("[Use Default]");
		
		chooseImage = new Button("Choose Image");
		render = new Button("Render");
		
		chooseImage.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent ae) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose File");
				
				File selectedFile = fileChooser.showOpenDialog(new Stage());
				
				if (selectedFile != null) {
					String path = selectedFile.toString();
					
					if (ImageLoader.testPath(path) == 0)
						curImagePath = path;
				}
			}
		});
		
		render.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent ae) {
				
				if (curImagePath != null) {
					String outputPath = "D:\\Programming\\Projects\\JavaProjects\\Personal\\AsciiArtMaker\\output";
					int charWidth = Integer.parseInt(charWidthField.getText());
					boolean invertedShading = Boolean.parseBoolean(invShadingField.getText());
					String palette = paletteField.getText();
					
					AsciiImage asciiImage = new AsciiImage(curImagePath, charWidth, invertedShading);
					// Alerts
					
					if (!palette.equals("[Use Default]")) {
						if (asciiImage.setPalette(palette) != 0) {
							Alert alert = new Alert(AlertType.ERROR, "Palette must be divisible by 256.");
							alert.showAndWait();
							return;
						}
						
					}
					
					String art = asciiImage.generateInParallel();
					
					try {
						ConsoleApp.writeToOutput(art, outputPath, asciiImage.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		
		gridPane.add(charWidthLabel, 0, 0);
		gridPane.add(charWidthField,  1, 0);
		gridPane.add(invShadingLabel,  0,  1);
		gridPane.add(invShadingField,  1,  1);
		gridPane.add(paletteLabel, 0, 2);
		gridPane.add(paletteField, 1, 2);
		gridPane.add(chooseImage, 0, 3);
		gridPane.add(render,  1,  3);
		
		stage.setTitle("AsciiArtMaker GUI Demo");
		stage.getIcons().add(icon);
		stage.setScene(appScene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
