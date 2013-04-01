package bench.demo1;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 */
class Browser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();

	public Browser() {
		// apply the styles
		getStyleClass().add("browser");
		// load the web page
		webEngine.load("http://www.google.com/");
		// add the web view to the scene
		getChildren().add(browser);

	}

	private Node createSpacer() {
		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	@Override
	protected void layoutChildren() {
		final double w = getWidth();
		final double h = getHeight();
		layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(final double height) {
		return 750;
	}

	@Override
	protected double computePrefHeight(final double width) {
		return 500;
	}

}