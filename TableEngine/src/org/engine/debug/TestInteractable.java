package org.engine.debug;

import org.engine.geometry.Rectangle;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicObject;

import com.badlogic.gdx.graphics.Color;

public class TestInteractable extends BasicObject{

	public TestInteractable() {
		
		super(new Rectangle(100,100, 100, 100), 1, 0, 90);
		abilities.add(MODE_SHIFT);
		abilities.add(MODE_ROTATE);
		abilities.add(MODE_RESIZE);
		abilities.add(MODE_SCALE);
		angle.set(90);
		rotateGrid.set(45);
		scale.set(1.5F);
		scaleGrid.set(0.25F);
		shiftGrid.set(25);
		resizeGrid.set(25);
	}
	
	@Override
	protected void paintTransformed(Graphics g) {

		super.paintTransformed(g);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width, height);
		g.drawString("TEST_OBJ...", 10, 40);
		g.drawRect(width/2 -2, height/2 -2, 4, 4);
	}
}
