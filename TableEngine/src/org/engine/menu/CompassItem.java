package org.engine.menu;

import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.gui.TransformGUI.TransformView;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicInteractable;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CompassItem extends BasicInteractable implements Skinnable, MenuItem {

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
		abilities.add(BasicInteractable.MODE_ROTATE);
	}

	@Override
	public void output(final Graphics g) {

		if (visible) {
			updateTransform();
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

		g2d.drawUI(style.icon, 0, 0, style.icon.getMinWidth(),
				style.icon.getMinHeight());
	}

	@Override
	protected void paintUntransformed(final Graphics g) {

		g.drawUI(style.background, x, y, width,
				height);
		if (over || rotating) {
			if (rotating) {
				g.drawUI(style.down, x, y, width,
						height);
			} else {
				g.drawUI(style.over, x, y, width,
						height);
			}
		} else {
			g.drawUI(style.up, x, y, width, height);
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
	public void style(Skin skin) {

		style = skin.get("default", CompassItemStyle.class);
		width = style.icon.getMinWidth();
		height = style.icon.getMinHeight();
	}

	@Override
	public void style(Skin skin, String style) {

		this.style = skin.get(style, CompassItemStyle.class);
		width = this.style.icon.getMinWidth();
		height = this.style.icon.getMinHeight();
	}

	@Override
	public void style(Style style) {

		if (CompassItemStyle.class.isAssignableFrom(style.getClass())) {

			this.style = (CompassItemStyle) style;
			width = this.style.icon.getMinWidth();
			height = this.style.icon.getMinHeight();
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		setPosition(MathUtils.ceil(width / 2 - this.width / 2),
				MathUtils.ceil(height / 2 - this.height / 2));
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
