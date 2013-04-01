package org.apache.pivot.swing;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JWindow;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Container;
import org.apache.pivot.wtk.ContainerListener;
import org.apache.pivot.wtk.DesktopFrame;
import org.apache.pivot.wtk.DesktopFrame.DragListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Panel;
import org.apache.pivot.wtk.Skin;
import org.apache.pivot.wtk.TitleBar;
import org.apache.pivot.wtk.skin.PanelSkin;


/**
 * <pre>
 *     
 *     
 *     -------------------------- o
 *     |  o                      |  o  	   <---- Apache Pivot Component residing in the Pivot host component.
 *     |       o                 |     o
 *     |            o  -------------------
 *     |               |                 | 
 *     |               |                 |
 *     |               |                 | <---- Swing JWindow delegate which receives size information
 *     |               |                 |		 and calculates location information translated to screen
 *     |               |                 |		 coordinates from messages delegated from the Apache Pivot
 *     |               |                 |       component.
 *     |               |                 |
 *     |               |                 |
 *	   |               |                 |         
 *	   ----------------|                 |
 *       o             |                 |
 *             o       |                 |
 *                   o -------------------
 *
 *
 *
 * Apache Pivot container which resides within the layout of the parent host.
 * This container is never seen due to the delegate Swing JWindow which covers
 * it, maintaining size and position information delegated to it from the 
 * underlying Pivot container. 
 * 
 * Because the underlying ui upon receiving focus will come to the front
 * of the delegate window, a low-level mouse listener is added so that the
 * delegate window will always remain in front therefore making it appear
 * as if the Swing component is actually inside the Apache container.
 * 
 * When the window containing the parent ui is moved, there will be a delay
 * within which the delegate window will not move. Once the parent window has
 * stopped moving briefly, the delegate window will update its position (or size
 * in the case of resizing) to the "delegator's" position (or size).
 * 
 * One approach to dealing with this drawback is to fork the code providing
 * an "undecorated frame" which subclasses {@link org.apache.pivot.wtk.DesktopApplicationContext.HostFrame}.
 * The next step is to paint your own titlebar and add {@link MouseListener} to it 
 * which can receive drag events and update the position of the frame (also causing
 * the delegate window to update ongoingly and thus keeping the ui positioning 
 * in sync). see {@link org.apache.pivot.wtk.DesktopFrame} for details
 *
 *
 * @author David Ray
 * @see org.apache.pivot.wtk.DesktopFrame
 */
public class SwingContainer extends Container {
	private Skin skin;
	private Delegate delegate;
	Display display;
	private java.awt.Window topLevelWindow;
	private java.awt.Insets insets;
	private javax.swing.Timer visibilityTimer;
	
	
	/**
	 * Creates a new {@code SwingContainer}
	 * @param parent
	 */
	public SwingContainer(final Container parent) {
		installSkin(Panel.class);
		delegate = new Delegate();
		
		
		parent.getContainerListeners().add(createContainerListener());
		
		//Keeps the delegate window in front.
//		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//			@Override
//			public void eventDispatched(AWTEvent event) {
//				if(event.getID() == MouseEvent.MOUSE_PRESSED) {
//					delegate.toFront();				
//				}
//			}
//		}, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		
//		(new Thread() {
//			public void run() {
//				while ((display = parent.getDisplay()) == null) {
//					try { Thread.sleep(2000); } catch(Exception e) { e.printStackTrace(); }
//				}
//				topLevelWindow = display.getHostWindow();
//				if(topLevelWindow instanceof DesktopFrame) {
//					((DesktopFrame)topLevelWindow).addDragListener(new DragListener() {
//						public void windowDragged(Point oldLoc, Point newLoc) {
//							delegate.setVisible(false);
//						}
//						public void draggingStopped() {
//							Point p = getLocationOnScreen();
//							moveWindow(p.x, p.y);
//							delegate.setVisible(true);
//						}
//					});
//					((DesktopFrame)topLevelWindow).addWindowStateListener(new WindowStateListener() {
//						@Override
//						public void windowStateChanged(WindowEvent e) {
//							if((e.getNewState() & JFrame.ICONIFIED) == JFrame.ICONIFIED) {
//								delegate.setVisible(false);
//							}else{
//								delegate.setVisible(true);
//								delegate.toFront();
//							}
//						}
//					});
//					((DesktopFrame)topLevelWindow).getTitleBar().addIconizeListener(new TitleBar.IconizeListener() {
//						@Override
//						public void frameWillIconize() {
//							delegate.setVisible(false);
//						}
//					});
//				}
//				topLevelWindow.addComponentListener(new ComponentAdapter() {
//					@Override
//					public void componentMoved(ComponentEvent e) {
//						Point p = getLocationOnScreen();
//						moveWindow(p.x, p.y);
//					}
//				});
//			}
//		}).start();
		
//		visibilityTimer = new javax.swing.Timer(250, new java.awt.event.ActionListener() {
//			public void actionPerformed(java.awt.event.ActionEvent e) {
//				delegate.setVisible(true);
//				delegate.requestFocus();
//			}
//		});
//		
//		visibilityTimer.setRepeats(false);
		
	}
	
	/**
	 * Listener which ensures that this "layered" container is visible.
	 * @return	ContainerListener
	 */
	private ContainerListener createContainerListener() {
		return new ContainerListener.Adapter() {
			@Override
			public void componentInserted(Container container, int index) {
				delegate.setVisible(true);
			}
		};
	}
	
	
	/**
	 * Routes size information to the delegate
	 * 
	 * @param	w	width 
	 * @param	h	height
	 */
	public void setSize(int w, int h) {
		//visibilityTimer.restart();
		//delegate.setVisible(false);
		super.setSize(w,h);
		delegate.setSize(w,h);
		delegate.toFront();
	}
	
	/**
	 * Routes location information to the delegate.
	 * 
	 * @param	x coordinate
	 * @param	y coordinate
	 */
	public void setLocation(int x, int y) {
		super.setLocation(x,y);
		java.awt.Point convertedPoint = getLocationOnScreen();
		moveWindow(convertedPoint.x, convertedPoint.y);
	}
	
	/**
	 * Method used to add the Swing component to be hosted
	 * by this container.
	 * 
	 * @param c		a java.awt.Component
	 */
	public void addSwingComponent(java.awt.Component c) {
		if(c == null) {
			throw new IllegalArgumentException("Component \"c\" cannot be null");
		}
		java.awt.Component child = delegate.getChild();
		if(child != null) {
			delegate.remove(child);
		}
		delegate.add(c);
	}
	
	/**
	 * Calculates the screen coordinates of this container which
	 * are used to relocate the delegate window.
	 * 
	 * @return	java.awt.Point 	the new screen coordinates of the delegate
	 */
	public java.awt.Point getLocationOnScreen() {
		int xLocal = 0;
        int yLocal = 0;

        Component component = this;
        while (component != null && component.isVisible()) {
        	if(component instanceof Display) { 
        		java.awt.Window window = ((Display)component).getHostWindow();
        		xLocal += window.getX();
                yLocal += window.getY();
                if(window instanceof DesktopFrame) {
            		insets = ((DesktopFrame)window).getTitlebarInsets();
            	}else{
            		insets = window.getInsets();
            	}
                xLocal += insets.left;
                yLocal += insets.top;
                break;
        	}else{
        		xLocal += component.getX();
        		yLocal += component.getY();
        	}
        	component = component.getParent();
        }
        
        return new java.awt.Point(xLocal, yLocal);
	}
	
	/**
	 * Moves the delegate window to the specified coordinates.
	 * 
	 * @param x		the horizontal coordinate
	 * @param y		the vertical coordinate
	 */
	public void moveWindow(int x, int y) {
		delegate.setLocation(x, y);
		delegate.toFront();
	}
	
	/**
	 * Resizes the delegate window to the specified size.
	 * 
	 * @param w		the new delegate width
	 * @param h		the new delegate height
	 */
	public void resizeWindow(int w, int h) {
		delegate.setSize(w,h);
		delegate.toFront();
	}
	
	/**
	 * Overridden to install a NOOP skin basically
	 * 
	 * @param		componentClass 	ignored
	 */
	@Override
    protected void installSkin(Class<? extends org.apache.pivot.wtk.Component> componentClass) {
    	skin = new PanelSkin();
    	setSkin(skin);
    }
	
	/**
	 * 	Delegate {@link JWindow} which contains the Swing component which
	 * is kept in sync with the Apache Pivot component {@link SwingContainer}
	 * @author David Ray
	 */
	@SuppressWarnings("serial")
	private class Delegate extends JWindow {
		private java.awt.Component child;
		
		public Delegate() {}
		
		@Override
		public java.awt.Component add(java.awt.Component c) {
			this.child = c;
			return super.add(c);
		}
		
		@Override
		public void setSize(int w, int h) {
			super.setSize(w, h);
			child.setSize(w, h);
		}
		
		private java.awt.Component getChild() {
			return child;
		}
		
		@Override
		public void remove(java.awt.Component c) {
			super.remove(c);
			if(c == child) {
				child = null;
			}
		}
	}

}
