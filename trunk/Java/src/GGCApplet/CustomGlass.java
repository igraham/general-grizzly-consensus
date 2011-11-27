package GGCApplet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * This creates the glass pane in a way to give a glossy look for the application without
 * interfering with buttons and objects. There Are three gradients used, White To Clear at
 * the top, White to Clear on the upper right corner, and light GGC Green to Clear at
 * the bottom. The gradients are placed in the RoundRect boarder.
 * 
 * @author Marcus Michalske
 *
 */
public class CustomGlass extends JComponent
{		
	CustomGlass(JFrame f)
	{
		CustomGlass.this.repaint();
	}
	
	public void paint(Graphics g)
	{
		float[] dist = {0.0f, 1.0f};
		Color[] colors = {new Color(205,205,205,100), new Color(0,0,0,0)};
		
		Graphics2D g2 = (Graphics2D) g.create();
		
		g2.setStroke(new BasicStroke(36));
		g2.setPaint(new RadialGradientPaint(new Point(getWidth()/2, getY()), getWidth(), dist, colors, CycleMethod.NO_CYCLE));
		g2.drawRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
		g2.setPaint(new RadialGradientPaint(new Point(getWidth(), getY()), 30, dist, colors, CycleMethod.NO_CYCLE));
		g2.fillRoundRect(getWidth()-35, 0, 200, 200, 30, 30);
		colors[0] = new Color(0, 175, 75);
		g2.setPaint(new RadialGradientPaint(new Point(getWidth()/2, getHeight()), getWidth()/2, dist, colors, CycleMethod.NO_CYCLE));
		g2.drawRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
		
		super.paintComponents(g);
	}
}