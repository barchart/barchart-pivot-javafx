package bench.demo3;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import com.sun.javafx.application.PlatformImpl;

/**
 * SwingFXWebView
 * <p>
 * https://gist.github.com/anjackson/1640654
 */
public class AwtFXWebView extends Container {

	private Stage stage;
	private WebView browser;
	private JFXPanel jfxPanel;
	private WebEngine webEngine;

	public AwtFXWebView() {
		initComponents();
	}

	public static void main(final String... args) {
		// Run this later:
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				final Frame frame = new Frame();

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						System.exit(0);
					}
				});

				frame.add(new AwtFXWebView());
				frame.setMinimumSize(new Dimension(640, 480));
				frame.setVisible(true);

			}
		});
	}

	private void initComponents() {

		jfxPanel = new JFXPanel();
		createScene();

		setLayout(new BorderLayout());
		add(jfxPanel, BorderLayout.CENTER);

	}

	/**
	 * createScene
	 * 
	 * Note: Key is that Scene needs to be created and run on "FX user thread"
	 * NOT on the AWT-EventQueue Thread
	 * 
	 */
	private void createScene() {
		PlatformImpl.startup(new Runnable() {
			@Override
			public void run() {

				stage = new Stage();

				stage.setTitle("Hello Java FX");
				stage.setResizable(true);

				final Group root = new Group();
				final Scene scene = new Scene(root, 80, 20);
				stage.setScene(scene);

				// Set up the embedded browser:
				browser = new WebView();
				webEngine = browser.getEngine();
				webEngine.load("http://www.google.com");

				final ObservableList<Node> children = root.getChildren();
				children.add(browser);

				jfxPanel.setScene(scene);
			}
		});
	}

}