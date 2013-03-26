package org.apache.pivot.javafx;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.w3c.dom.Document;

import com.sun.javafx.application.PlatformImpl;


/**
 * A {@link javax.swing.JComponent} specifically tailored to host a {@link WebView}
 * component and expose methods to manage the web browsing experience. This component
 * subclasses {@link JFXPanel} in order to make the {@code WebView} component
 * embeddable within a Swing component, and exposes through delegation those 
 * methods relevant to control of the underlying {@code WebView} browser engine.
 * 
 * @author cogmission
 *
 */
@SuppressWarnings("serial")
public class FXWebView extends JFXPanel {
	private Stage stage;
	private WebView browser;
	private WebEngine webEngine;
	private final String homeUrl;
	private StackPane root;
	
	
	/**
	 * Constructs a new FXWebView. 
	 * 
	 * @param url
	 */
	public FXWebView(String url) {
		//Validate the passed in url
		URL u;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
		this.homeUrl = u.toExternalForm();
		
		//Construct the underlying scene
		createScene();
	}
	
	/**
	 * Adds an {@link ChangeListener to the underlying {@link WebEngine}'s
	 * documentProperty. 
	 * @param l
	 */
	public void addDocumentChangeListener(ChangeListener<Document> l) {
		webEngine.documentProperty().addListener(l);
	}
	
	/**
	 * Sets the size of this component and delegates
	 * the size specified to the underlying {@link Scene}
	 * <i>
	 * <b>WARNING: This method is called by the parent layout manager
	 * and directly delegates to the underlying {@link WebView} </b>
	 * node.
	 * </i>
	 * 
	 * @param	w 	the width
	 * @param	h 	the height
	 */
	public void setSize(final int w, final int h) {
		super.setSize(w, h);
		Platform.runLater(new Runnable() {
			public void run() {
				browser.setPrefSize(w, h);
			}
		});
	}
	
	/**
	 * Returns the {@link WebEngine}'s {@link Worker} thread
	 * which handles document loading progress monitoring 
	 * and reporting.
	 * 
	 * @return	the WebEngine's load worker
	 */
	public Worker<java.lang.Void> getLoadWorker() {
		return webEngine.getLoadWorker();
	}
	
	/**
	 * Returns the underlying {@link WebEngine}.
	 * @return
	 */
	public WebEngine getWebEngine() {
		return webEngine;
	}
	
	/**
	 * Validates then loads the specified {@link URL} ensuring
	 * that the message is made on the JavaFX Application  Thread.
	 * 
	 * @param url	a url String which will be validated
	 * @throws	IllegalArgumentException when the specified URL is invalid
	 */
	public void loadUrl(final String urlString) {
		URL urlObject = null;
		try {
			urlObject = new URL(urlString);
		}catch(MalformedURLException ex) {
			throw new IllegalArgumentException(ex);
		}
		
		final String url = urlObject.toString();
		if(Platform.isFxApplicationThread()) {
			loadUrlInternal(url);
		}else {
			Platform.runLater(new Runnable() {
				public void run() {
					loadUrlInternal(url);
				}
			});
		}
	}
	
	/**
	 * Loads a String containing a JavaScript script into
	 * the webEngine. The script is executed in the context of
	 * the current page.
	 * 
	 * @param javaScript	string containing JavaScript
	 */
	public void executeScript(final String javaScript) {
		if(Platform.isFxApplicationThread()) {
			webEngine.executeScript(javaScript);
		}else {
			Platform.runLater(new Runnable() {
				public void run() {
					webEngine.executeScript(javaScript);
				}
			});
		}
	}
	
	
	
	/**
	 * Loads the specified URL. This method is called
	 * internally and must be called on the JavaFX Application 
	 * Thread
	 * 
	 * @param url	URL string.
	 */
	private void loadUrlInternal(String url) {
		webEngine.load(url);
	}

	/**
	 * Removes the gray overlay mask that is layered on 
	 * top of the web view after calling the {@link #overlayView(Node)}
	 * method.
	 */
	public void removeViewOverlay() {
		StackPane parent = (StackPane)browser.getParent();
		parent.getChildren().remove(1);
	}
	
	/**
	 * Overlay a dialog on top of the webview.
	 * @param dialogNode the dialog to overlay on top of the view.
	 */
	public void overlayView(Node dialogNode) {
	    // if the view is already overlaid we will just ignore this overlay call silently . . . todo probably not the best thing to do, but ok for now.
		if (((StackPane)browser.getParent()).getChildren().size() > 1) return;
	
		// record the view's parent.
		StackPane parent = (StackPane)browser.getParent();
		
		BorderPane center = new BorderPane();
		center.setMaxSize(300, 200);
		center.setCenter(dialogNode);
		browser.setDisable(true);
		center.setStyle("-fx-background-color: #CCCCCC");
		parent.getChildren().add(center);
	}
	
	/**
	 * Creates the underlying JavaFX {@link WebView}
	 * and {@link WebEngine} within a the constructed
	 * {@link Scene} and {@link Stage}
	 */
	private void createScene() {
		PlatformImpl.startup(new Runnable() {
			@Override
			public void run() {
				root = new StackPane();
				Scene scene = new Scene(root, 0, 0);
				
				stage = new Stage();
				stage.setTitle("Hello Java FX");
				stage.setResizable(true);
				stage.setScene(scene);
				
				// Set up the embedded browser:
				browser = new WebView();
				
				//Set an blurred overlay effect when the disabled property becomes true.
				browser.disabledProperty().addListener(new ChangeListener<Boolean>() {
				    final BoxBlur     soften = new BoxBlur();
				    final ColorAdjust dim    = new ColorAdjust();
				    
				    { 
				    	dim.setInput(soften); dim.setBrightness(-0.5); 
				    }
				    @Override 
				    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
				    	if (newValue) {
				    		browser.setEffect(dim);
				    	} else {
				    		browser.setEffect(null);
				    	}
				    }
			    });
				
				webEngine = browser.getEngine();
				
				root.getChildren().add(browser);
				
				setScene(scene);
				
				loadUrl(homeUrl);
			}
		});
	}
}
