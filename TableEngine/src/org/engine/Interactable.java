package org.engine;

import org.engine.Skinnable.Style;
import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Interface that provides IO functions to an object in order to work properly
 * in one of the TableEngine's layers.
 */
public interface Interactable {

	/**
	 * Checks if a point is inside this object's bounds.
	 * 
	 * @param point
	 *            the point
	 * @return true, if is inside bounds.
	 */
	public boolean isColliding(Vector2 point);

	/**
	 * Checks if a transformed Rectangle is intersecting this object's bounds.
	 * 
	 * @param r
	 *            the Rectangle
	 * @param angle
	 *            the rotation angle of the Rectangle
	 * @param scale
	 *            the scale factor of the Rectangle
	 * @return true, if is intersecting
	 */
	public boolean isColliding(Rectangle r, int angle, float scale);

	/**
	 * Return the depth of this object, this is taken by the objects parent
	 * layer ({@link #getParentLayer()} for drawing and managing input.
	 * 
	 * @return the depth
	 */
	public float depth();

	/**
	 * Gets the parent layer. That is the Layer this object is registered to.
	 * When adding an Interactable object to a Layer it will call
	 * {@link #initialize(Layer)}.
	 * 
	 * @return the parent layer
	 */
	public Layer getParentLayer();

	/**
	 * Initializes this object. Marks it as belonging to the Layer. Called
	 * automatically after adding this object to the layer via
	 * {@link Layer#addInteractable(Interactable)}.
	 * 
	 * @param layer
	 *            the layer this object is assigned to (via
	 *            {@link Layer#addInteractable(Interactable)}). Calling
	 *            {@link #getParentLayer()} has to return this layer.
	 */
	public void initialize(Layer layer);

	/**
	 * Forwards an {@link InputEvent} to this object. Called automatically by
	 * the parent layer ({@link #getParentLayer()}) after the object has been
	 * added to it via {@link Layer#addInteractable(Interactable)}. This method
	 * is not called during the render method, therefore rendering stuff to the
	 * openGL channel is not possible. Call
	 * {@link com.badlogic.gdx.Graphics#requestRendering()} from
	 * {@link com.badlogic.gdx.Gdx#graphics} instead.
	 * 
	 * Usually the class should check whether the InputEvent happened inside
	 * it's bounds and if so process it and return true. It should return false
	 * if the InputEvent is outside the objects bounds.
	 * 
	 * @param e
	 *            the InputEvent.
	 * @param wasCatchedAbove
	 *            true if already processed.
	 * @return true, if the InputEvent was processed by this object.
	 */
	public boolean input(InputEvent e, boolean wasCatchedAbove);

	/**
	 * Forwards a {@link Graphics} object to this object. Called automatically
	 * by the parent layer ({@link #getParentLayer()}) after the object has been
	 * added to it via {@link Layer#addInteractable(Interactable)}.
	 * 
	 * Here the object should draw it's graphical equivalent to the correct
	 * position on the Graphics object.
	 * 
	 * @param g
	 *            the Graphics object.
	 */
	public void output(Graphics g);

	/**
	 * Animations can be assigned to an Animatable object via a String description. This String is 
	 */
	public interface Animateable extends Interactable {

		/**
		 * Animate.
		 * 
		 * @param animationString
		 *            the animation string
		 */
		public void animate(String animationString);

	}

	/**
	 * The style for Interactable objects. 
	 */
	public static class InteractableStyle extends Style {

		public Drawable background, up, down, over, selected, shift, resize,
				rotate, scale;

		/**
		 * Instantiates a new interactable style without loading Drawables.
		 */
		public InteractableStyle() {

		}

		/**
		 * Instantiates a new interactable style from Drawables.
		 * 
		 * @param background
		 *            the background Drawable
		 * @param up
		 *            the up Drawable
		 * @param down
		 *            the down Drawable
		 * @param over
		 *            the over Drawable
		 * @param selected
		 *            the selected Drawable
		 * @param shift
		 *            the shift icon Drawable
		 * @param resize
		 *            the resize icon Drawable
		 * @param rotate
		 *            the rotate icon Drawable
		 * @param scale
		 *            the scale icon Drawable
		 */
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

		/**
		 * Instantiates a new interactable style from another.
		 * 
		 * @param style
		 *            the style to adopt Drawables from.
		 */
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
