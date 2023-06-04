package com.york;
	
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.york.model.AsciiArt;
import com.york.model.AsciiImage;
import com.york.model.Settings;
import com.york.model.console.Mode;
import com.york.model.media.ImagePathAdapter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;


public class App extends Application {

	private ImagePathAdapter pathAdapter;

	private TextField charWidthField;
	private CheckBox invShadingField;
	private TextField paletteField;

	private String getPathOfResource(String subPath) {
		return Objects.requireNonNull(
				getClass().getResource(subPath)
		).toString();
	}

	// TODO: belongs in view
	@Override
	public void start(Stage stage) {
		Image icon = new Image(getPathOfResource("/com/york/images/icon.png"));
		pathAdapter = null;
		
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		
		Scene appScene = new Scene(gridPane);

		Label charWidthLabel = new Label("Character Width:");
		charWidthField = new TextField();
		Label invShadingLabel = new Label("Invert Shading:");
		invShadingField = new CheckBox();
		Label paletteLabel = new Label("Palette:");
		paletteField = new TextField();
		
		charWidthField.setPrefColumnCount(2);
		charWidthField.setEditable(true);
		charWidthField.setText("1");
		
		paletteField.setPrefColumnCount(16);
		paletteField.setEditable(true);
		paletteField.setText(AsciiArt.DEFAULT_PALETTE);

		Button chooseImage = new Button("Choose Image");
		Button render = new Button("Render");
		
		chooseImage.setOnAction(ae -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose File");
			File selectedFile = fileChooser.showOpenDialog(new Stage());
			if (selectedFile == null) {
				new Alert(AlertType.ERROR, "Unexpected Error: file not found.").showAndWait();
			}
			else {
				String path = selectedFile.toString();
				if (ImagePathAdapter.testPath(path) != ImagePathAdapter.FILE_NOT_ACCEPTED) {
					try {
						pathAdapter = new ImagePathAdapter(path);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		render.setOnAction(ae -> {
			if (pathAdapter != null) {
				int charWidth = Integer.parseInt(charWidthField.getText());
				String palette = paletteField.getText();

				AsciiImage asciiImage = new AsciiImage(pathAdapter)
						.setName(pathAdapter.getImageName())
						.setCharWidth(charWidth)
						.setInvertedShading(invShadingField.isSelected());
				try {
					asciiImage.setPalette(palette);
				} catch (IllegalArgumentException e) {
					new Alert(AlertType.ERROR, e.toString());
				}

				try {
					String outPutPath = Mode.writeImageToOutput(asciiImage, Settings.DOWNLOADS);
					new Alert(AlertType.INFORMATION, "art has been output to: " + outPutPath).showAndWait();

				} catch (IOException e) {
					new Alert(AlertType.ERROR, "Unexpected Error with writing to output.").showAndWait();
					e.printStackTrace();
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
