package GGCApplet;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is to make custom JButton graphics that will be uniform in the whole application.
 * The button has three rounded rectangles, the outline is one color, the inner rectangle is a
 * gradient of two colors, and the inner most rectangle is a gradient of two colors.
 * @author Marcus Michalske
 *
 */
public class CustomJToggleButton extends JToggleButton implements ChangeListener
{
	int state = 1;
	int inactive=0;

	public CustomJToggleButton(String s)
	{
		super(s);
		setHorizontalAlignment(SwingConstants.CENTER);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setBorderPainted(false);
		this.addChangeListener(this);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		Graphics2D g3 = (Graphics2D) g.create();

		AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
		g3.setComposite(newComposite);

		g2.setPaint(Color.LIGHT_GRAY);
		g2.setStroke(new BasicStroke(1));
		//g2.setPaint(new GradientPaint(new Point(0, 0), Color.DARK_GRAY, new Point(0, getHeight()), Color.WHITE));
		g2.fillRoundRect(0, 0, getWidth()-1, getHeight(), 10, 10);
		g2.setPaint(Color.GRAY);
		g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

		Point2D center = new Point2D.Float(getWidth()/2, getHeight()/2);
		float radius = getWidth();
		float[] dist = {0.0f, 1.0f};
		Color[] colors = {Color.WHITE, new Color(0,0,0,0)};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors, CycleMethod.NO_CYCLE);

		g3.setPaint( p );
		g3.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 5, 5);

		if(inactive==1)
		{
			//Dummy space
		}
		if(state==3)
		{
			colors[0] = Color.DARK_GRAY;
			colors[1] = Color.WHITE;
			p = new RadialGradientPaint(center, radius-10, dist, colors, CycleMethod.NO_CYCLE);
			g3.setPaint( p );
			g3.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
		}

		g2.dispose();
		g3.dispose();

		super.paintComponent(g);
	}
	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
		ButtonModel buttonModel = abstractButton.getModel();
		boolean armed = buttonModel.isArmed();
		boolean pressed = buttonModel.isPressed();
		boolean selected = buttonModel.isSelected();
		if(selected)
		{
			state=3;
			repaint();
		}
		else if(pressed)
		{
			state=3;
			repaint();
		}
		else
		{
			state=2;
			repaint();
		}
	}

}
