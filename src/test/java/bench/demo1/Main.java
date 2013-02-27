package bench.demo1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 */
public class Main extends Application {

	private Scene scene;

	@Override
	public void start(final Stage stage) {
		// create the scene
		stage.setTitle("Web View");
		scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
		stage.show();
	}

	public static void main(final String[] args) {
		launch(args);
	}

}
