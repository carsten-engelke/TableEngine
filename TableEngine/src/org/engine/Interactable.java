package org.engine;

import org.engine.Skinnable.Style;
import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public interface Interactable {

	float START_Z_VALUE = 10000;
	
	public boolean isColliding(Vector2 point);
	
	public boolean isColliding(Rectangle r, int angle, float scale);
	
	public float depth();

	public Layer getParentLayer();

	public void initialize(Layer layer);

	public boolean input(InputEvent e, boolean wasCatchedAbove);

	public void output(Graphics g);
	
	public interface Animateable extends Interactable {

		public void animate(String animationString);

	}
	
	public static class InteractableStyle extends Style {

		public Drawable background, up, down, over, selected, shift, resize,
				rotate, scale;

		public InteractableStyle() {

		}

		public InteractableStyle(final Drawable background, final Drawable up,
				final Drawable down, final Drawable over,
				final Drawable selected, final Drawable shift,
				final Drawable resize, final Drawable rotate,
				final Drawable scale) {

			this.background = background;
			this.up = up;
			this.down = down;
			this.over = over;
			this.selected = selected;
			this.shift = shift;
			this.resize = resize;
			this.rotate = rotate;
			this.scale = scale;
		}

		public InteractableStyle(final InteractableStyle style) {

			background = style.background;
			up = style.up;
			down = style.down;
			over = style.over;
			selected = style.selected;
			shift = style.shift;
			resize = style.resize;
			rotate = style.rotate;
			scale = style.scale;
		}
	}
}
