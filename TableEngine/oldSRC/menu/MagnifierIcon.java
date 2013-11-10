package org.engine.menu;

import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicObject;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class MagnifierIcon extends BasicObject {

	public static final int iconID = 1;

	private MagnifierIconStyle style;

	public boolean visible;

	private boolean over = false;

	private boolean down = false;

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		if (visible) {
			// && !wasCatchedAbove
			// && (e.getType() == InputEvent.TYPE_CLICK_POS)
			// && (e.getCharacter() == InputEvent.CHAR_NORMAL_POS)
			// && getRectangle2D().contains(e.getX(), e.getY())
			// && (parentUniverse.getMagnifier().getFocussedObject() != null)) {

			wasCatchedByMe = super.input(e, wasCatchedAbove);
		}
		return wasCatchedByMe;
	}

	@Override
	public boolean posClicked(InputEvent e) {

		if (parentUniverse.getMagnifier().getFocussedObject() != null) {
			getParentLayer().getParentUniverse().getMagnifier()
					.setMagnificating(1.0F);
			over = false;
			down = false;
		}
		return super.posClicked(e);
	}

	@Override
	public boolean posEntered(InputEvent e) {

		over = true;
		return super.posEntered(e);
	}

	@Override
	public boolean posPressed(InputEvent e) {

		down = true;
		return super.posPressed(e);
	}

	@Override
	public boolean posExited(InputEvent e) {

		over = false;
		down = false;
		return super.posExited(e);
	}

	@Override
	public void output(Graphics g) {

		if (visible) {

			super.output(g);
		}
	}

	@Override
	protected void paintTransformed(final Graphics g2d) {

		g2d.drawUI(style.background, getX(), getY(), style.icon.getMinWidth(),
				style.icon.getMinHeight());
		if (over || down) {
			if (down) {
				g2d.drawUI(style.down, getX(), getY(),
						style.icon.getMinWidth(), style.icon.getMinHeight());
			} else {
				g2d.drawUI(style.over, getX(), getY(),
						style.icon.getMinWidth(), style.icon.getMinHeight());
			}
		} else {
			g2d.drawUI(style.up, getX(), getY(), style.icon.getMinWidth(),
					style.icon.getMinHeight());
		}
		g2d.drawUI(style.icon, getX(), getY(), style.icon.getMinWidth(),
				style.icon.getMinHeight());

	}

	@Override
	public void style(Skin skin) {

		this.style = skin.get("default", MagnifierIconStyle.class);
	}

	@Override
	public void style(Skin skin, String styleName) {

		style = skin.get(styleName, MagnifierIconStyle.class);
	}

	@Override
	public void style(Style style) {

		if (MagnifierIconStyle.class.isAssignableFrom(style.getClass())) {
			this.style = (MagnifierIconStyle) style;
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		setLocation(MathUtils.ceil(width / 2 - style.icon.getMinWidth() / 2),
				MathUtils.ceil(height / 2 - style.icon.getMinHeight() / 2),
				depth);
	}

	public static class MagnifierIconStyle extends InteractableStyle {

		public Drawable icon;

		public MagnifierIconStyle() {

		}

		public MagnifierIconStyle(Drawable icon) {

			this.icon = icon;
		}

		public MagnifierIconStyle(MagnifierIconStyle style) {

			super(style);
			this.icon = style.icon;
		}
	}
}
