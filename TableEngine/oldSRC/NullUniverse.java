package org.engine;

import org.engine.gui.output.Graphics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class NullUniverse {
	
	public void output(final Graphics g) {
		g.setColor(Color.RED);
		final String NOT_INITIATED = "Game not initiated. Please overwrite 'loadGame()' method of TableEngine";
		g.drawString(NOT_INITIATED, (Gdx.graphics.getWidth() - g.getFont()
				.getBounds(NOT_INITIATED).width) / 2,
				(Gdx.graphics.getHeight() - g.getFont()
						.getBounds(NOT_INITIATED).height) / 2);
	}
}
