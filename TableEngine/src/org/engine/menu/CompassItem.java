package org.engine.menu;

import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.gui.TransformGUI.TransformView;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicObject;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CompassItem extends BasicObject implements Skinnable, MenuItem {

	public static class CompassItemStyle extends InteractableStyle {

		public Drawable icon;

		public CompassItemStyle() {
			super();
		}

		public CompassItemStyle(final CompassItemStyle style) {

			super(style);
			icon = style.icon;
		}
	}
	
	public static final int iconID = 2;

	private boolean over = false;

	protected CompassItemStyle style;

	public boolean visible;

	@Override
	public void initialize(final Layer l) {

		super.initialize(l);
		abilities.add(BasicObject.MODE_ROTATE);
	}

	@Override
	public void output(final Graphics g) {

		if (visible) {
			updateAffineTransform();
			// setSize(getSizeIfTransformWas(untransformedSize, angle, scale)
			// .getSize());
			super.output(g);
		}
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		if (visible) {
			wasCatchedByMe = super.input(e, wasCatchedAbove);
		}
		return wasCatchedByMe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.object.RotateSynchronizable#paint(java.awt.Graphics2D,
	 * java.awt.Rectangle)
	 */
	@Override
	protected void paintTransformed(final Graphics g2d) {

		g2d.drawUI(style.icon, x, y, style.icon.getMinWidth(),
				style.icon.getMinHeight());
	}

	@Override
	protected void paintUntransformed(final Graphics g) {

		g.drawUI(style.background, x, y, untransformedSize.get().x,
				untransformedSize.get().y);
		if (over || rotating) {
			if (rotating) {
				g.drawUI(style.down, x, y, untransformedSize.get().x,
						untransformedSize.get().y);
			} else {
				g.drawUI(style.over, x, y, untransformedSize.get().x,
						untransformedSize.get().y);
			}
		} else {
			g.drawUI(style.up, x, y, untransformedSize.get().x, untransformedSize.get().y);
		}
	}
	
	@Override
	protected void paintForeground(Graphics g) {

	}

	@Override
	protected void paintForegroundUntransformed(Graphics g) {

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#mouseDragged(java.awt.event.
	 * MouseEvent )
	 */
	@Override
	public boolean posDragged(final InputEvent e) {

		final boolean wasCatchedByMe = super.posDragged(e);
		TransformView v = new TransformView(t.getPrefString("playerView"));
		v.angle = angle.get();
		t.preferences.putString("playerView", v.toString());
		return wasCatchedByMe;
	}

	@Override
	public boolean posEntered(final InputEvent e) {

		over = true;
		return super.posEntered(e);
	}

	@Override
	public boolean posExited(final InputEvent e) {

		over = false;
		return super.posExited(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#mouseReleased(java.awt.event.
	 * MouseEvent)
	 */
	@Override
	public boolean posReleased(final InputEvent e) {

		final boolean wasCatchedByMe = super.posReleased(e);
		TransformView v = new TransformView(t.getPrefString("playerView"));
		v.angle = angle.get();
		t.preferences.putString("playerView", v.toString());
		return wasCatchedByMe;

	}

	@Override
	protected void updateAffineTransform() {

		transformMatrix = new Matrix4();

		// look at the new borders -> transforming will set new borders so
		// estimate how much translation has to be done. Calculate the
		// translation into the rotated world of the image and apply it!
		float dX = getSizeIfTransformWas(untransformedSize.get(), angle.get(), scale.get())
				.getX();
		float dY = getSizeIfTransformWas(untransformedSize.get(), angle.get(), scale.get())
				.getY();

		dX = getX() + (untransformedSize.get().x / 2);
		dY = getY() + (untransformedSize.get().y / 2);
		transformMatrix.translate(dX, dY, 0);
		transformMatrix.rotate(0, 0, 1, angle.get());
		transformMatrix.translate(-dX, -dY, 0);

		// rotate image around the position of object
		invertedTransform = new Matrix4(transformMatrix);
		invertedTransform.inv();
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", CompassItemStyle.class);
		untransformedSize.get().x = style.icon.getMinWidth();
		untransformedSize.get().y = style.icon.getMinHeight();
		setSize(untransformedSize.get());
	}

	@Override
	public void style(Skin skin, String style) {

		this.style = skin.get(style, CompassItemStyle.class);
		untransformedSize.get().x = this.style.icon.getMinWidth();
		untransformedSize.get().y = this.style.icon.getMinHeight();
		setSize(untransformedSize.get());
	}

	@Override
	public void style(Style style) {

		if (CompassItemStyle.class.isAssignableFrom(style.getClass())) {

			this.style = (CompassItemStyle) style;
			untransformedSize.get().x = this.style.icon.getMinWidth();
			untransformedSize.get().y = this.style.icon.getMinHeight();
			setSize(untransformedSize.get());
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		setLocation(MathUtils.ceil(width / 2 - untransformedSize.get().x / 2),
				MathUtils.ceil(height / 2 - untransformedSize.get().y / 2), depth);
	}

	@Override
	public void setVisible(boolean b) {

		visible = b;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public int getPreferredPosition() {
		return 0;
	}

	@Override
	public String getLabel() {

		return Menu.COMPASS_ITEM;
	}
}
