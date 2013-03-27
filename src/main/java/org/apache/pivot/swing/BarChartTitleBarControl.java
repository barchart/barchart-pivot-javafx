package org.apache.pivot.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import org.apache.pivot.wtk.TitleBarControl;

@SuppressWarnings("serial")
public class BarChartTitleBarControl extends TitleBarControl {
	private double rotationAngle = 0.0d;
	private BufferedImage logo = createLogo(Color.DARK_GRAY, new Color(202, 202, 202));
	
	
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = 2;
        int y = 2;
        int imgCenterX = x + this.logo.getWidth() / 2;
        int imgCenterY = y + this.logo.getHeight() / 2 - 2;
        g2.translate(imgCenterX, imgCenterY);
        double angle = -getRotation();
        g2.rotate(angle);
        g2.translate(-imgCenterX, -imgCenterY);
        g2.drawImage(this.logo, x, y, null); 
    }
	
	private double getRotation() {
		return rotationAngle;
	}
	
	private BufferedImage createLogo(Color bg, Color fg) {
		BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)img.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int lx = 0;
		g2.setColor(bg);
        Arc2D lLogo = new Arc2D.Double(lx, 0, 16, 16, 90, 180, Arc2D.OPEN);
        g2.draw(lLogo);
        g2.drawLine(lx + 8, 0, lx + 8, 4);
        g2.drawLine(lx + 8, 4, lx + 4, 4);
        g2.drawLine(lx + 4, 4, lx + 4, 6);
        g2.drawLine(lx + 4, 6, lx + 8, 6);
        g2.drawLine(lx + 8, 6, lx + 8, 15);
                
        Arc2D rLogo = new Arc2D.Double(lx + 2, 0, 18, 17, 90, -180, Arc2D.OPEN);
        g2.fill(rLogo);
        g2.setColor(fg);
        g2.fillRect(lx + 10, 10, 4, 2);
        
        g2.dispose();
        return img;
	}
	
	public void mousePressed(MouseEvent m) {
		if(isControlHit(m)) {
			rotationAngle = Math.PI;
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent m) {
		rotationAngle = 0;
		repaint();
	}
}
