package bench.demo0;

import java.awt.Color;
import java.awt.Font;

import javafx.scene.web.WebView;

import javax.swing.SwingUtilities;

import org.apache.pivot.collections.Map;
import org.apache.pivot.javafx.FXWebView;
import org.apache.pivot.swing.SwingContainer;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.DesktopFrame;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.SplitPane;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.Window;


/**
 * Demos the ability to use a JavaFX {@link WebView} component within
 * a Apache Pivot container (in this case a {@link SplitPane})
 * 
 * @author David Ray
 */
public class PivotFXWebViewDemo extends Application.Adapter {

	private Window window;
	
	
	public PivotFXWebViewDemo() {
		window = new Window();
	}
	
	
	@Override
    public void startup(final Display display, Map<String, String> map) throws Exception {
		Label label = new Label();
        label.setText("Hello World!");
        label.getStyles().put("font", new Font("Arial", Font.BOLD, 24));
        label.getStyles().put("color", Color.RED);
        label.getStyles().put("backgroundColor", Color.BLUE);
        label.getStyles().put("horizontalAlignment", HorizontalAlignment.CENTER);
        label.getStyles().put("verticalAlignment", VerticalAlignment.CENTER);
        
        Label label2 = new Label();
        label2.setText("Goodbye World!");
        label2.getStyles().put("font", new Font("Arial", Font.BOLD, 24));
        label2.getStyles().put("color", Color.YELLOW);
        label2.getStyles().put("backgroundColor", Color.GREEN);
        label2.getStyles().put("horizontalAlignment", HorizontalAlignment.CENTER);
        label2.getStyles().put("verticalAlignment", VerticalAlignment.CENTER);
        
        SplitPane split = new SplitPane(Orientation.HORIZONTAL);
        split.setLeft(label);
        
        //Apache Pivot Container that can contain SWING components
        SwingContainer container = new SwingContainer();
        //Method used to add a Swing component to the container
        container.addSwingComponent(new FXWebView("http://yahoo.com"));
        //Simply add the SwingContainer to any Apache Pivot container
        split.setRight(container);
 
        window.setContent(split);
        window.setTitle("WebView Demo");
        window.setMaximized(true);
        
        window.setLocation(new Point(0,0));
        window.setPreferredSize(new Dimensions(1600, 900));
        
        //Show window.
        window.open(display);
        
        
        //Tomorrow build out osx and windows titlebars
        final java.awt.Window hostFrame = window.getDisplay().getHostWindow();
        
        (new Thread() {
        	public void run() {
        		try { Thread.sleep(4000); }catch(Exception e) { e.printStackTrace(); }
        		
        		SwingUtilities.invokeLater(new Runnable() {
        			public void run() {
        				if(hostFrame instanceof DesktopFrame) {
        					DesktopFrame.Style.NATIVE_WIN.apply((DesktopFrame)hostFrame);
        				}
        			}
        		});
        		
//        		try { Thread.sleep(4000); }catch(Exception e) { e.printStackTrace(); }
//        		
//        		SwingUtilities.invokeLater(new Runnable() {
//        			public void run() {
//        				if(hostFrame instanceof DesktopFrame) {
//        					DesktopFrame.Style.NATIVE_OSX.apply((DesktopFrame)hostFrame);
//        				}
//        			}
//        		});
        	}
        }).start();
    }
	
	public static void main(String[] args) {
    	DesktopApplicationContext.main(PivotFXWebViewDemo.class, args);
    }
}
