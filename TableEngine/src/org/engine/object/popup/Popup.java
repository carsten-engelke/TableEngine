package org.engine.object.popup;

import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicInteractable;
import org.engine.utils.Array;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Popup extends BasicInteractable {

	class Order {

		Rectangle bounds = new Rectangle();
		boolean down = false;
		String order;
		boolean over = false;

		Order(final String order) {

			this.order = order;
		}

		@Override
		public String toString() {

			return order;
		}
	}

	static public class PopupStyle extends InteractableStyle {

		public BitmapFont font;

		public Color fontColor, fontColorDown, fontColorOver;

		public PopupStyle() {

			super();
		}

		public PopupStyle(final PopupStyle style) {

			super(style);
			font = style.font;
			fontColor = style.fontColor;
			fontColorDown = style.fontColorDown;
			fontColorOver = style.fontColorOver;
		}

	}

	private boolean isSelected = false;

	public Array<Order> orders = new Array<Popup.Order>();

	private final PopupSlave slave;

	protected PopupStyle style;

	public Popup(final Array<String> orders, final PopupSlave slave) {

		super();
		for (final String s : orders) {
			this.orders.add(new Order(s));
		}
		this.slave = slave;
	}

	private void checkDown(final InputEvent e) {

		for (final Order o : orders) {
			o.over = false;
			if (o.bounds.contains(e.getX(), e.getY())) {
				o.down = true;
			} else {
				o.down = false;
			}
		}
	}

	private void checkOver(final InputEvent e) {

		for (final Order o : orders) {
			if (o.bounds.contains(e.getX(), e.getY())) {
				o.over = true;
			} else {
				o.over = false;
			}
		}
	}

	@Override
	public void initialize(final Layer l) {

		super.initialize(l);
		updateOrders();
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		final boolean wasCatchedByMe = super.input(e, wasCatchedAbove);
		if ((e.getType() == InputEvent.TYPE_TOUCH_CLICKED)
				&& !containsRelative(relativePosition)) {
			mouseClickedOutside();
		}
		return wasCatchedByMe;
	}

	private void mouseClickedOutside() {

		l.removeInteractable(this);
		slave.receiveOrder(PopupSlave.CANCEL_ORDER);
	}

	@Override
	protected void paintTransformed(final Graphics g) {

		g.setFont(style.font);
		g.setColor(Color.WHITE);
		g.drawUI(style.background, 0, 0, width,
				height);
		for (final Order o : orders) {

			if (o.over) {

				g.drawUI(style.over, o.bounds.x, o.bounds.y, o.bounds.width,
						o.bounds.height);
				g.setColor(style.fontColorOver);

			} else {

				if (o.down) {

					g.drawUI(style.down, o.bounds.x, o.bounds.y,
							o.bounds.width, o.bounds.height);
					g.setColor(style.fontColorDown);

				} else {
					g.drawUI(style.up, o.bounds.x, o.bounds.y, o.bounds.width,
							o.bounds.height);
					g.setColor(style.fontColor);
				}

			}
			g.drawString(o.order, o.bounds.x + 5, o.bounds.y + 5);
		}
	}

	@Override
	public boolean touchClick(final InputEvent e) {

		for (final Order o : orders) {
			if (o.bounds.contains(e.getX(), e.getY())) {
				l.removeInteractable(this);
				slave.receiveOrder(o.order);
			}
		}
		return super.touchClick(e);
	}

	@Override
	public boolean touchDrag(final InputEvent e) {

		if (isSelected) {

			checkDown(e);
			return true;

		} else {

			return super.touchDrag(e);

		}
	}

	@Override
	public boolean touchEnter(final InputEvent e) {

		checkOver(e);
		return super.touchEnter(e);
	}

	@Override
	public boolean touchExit(final InputEvent e) {

		for (final Order o : orders) {
			o.over = false;
			o.down = false;
		}
		return super.touchExit(e);
	}

	@Override
	public boolean touchMove(final InputEvent e) {

		checkOver(e);
		return super.touchMove(e);
	}

	@Override
	public boolean touchDown(final InputEvent e) {

		checkDown(e);
		isSelected = true;
		return true;
	}

	@Override
	public boolean touchUp(final InputEvent e) {

		if (isSelected) {

			return touchClick(e);

		} else {

			return super.touchUp(e);

		}
	}
	
	private void updateOrders() {

		final BitmapFont f = style.font;
		float cumulativeHeight = 0;
		float maxWidth = 0;
		for (final Order order : orders) {

			final TextBounds t = f.getBounds(order.order);

			order.bounds = new Rectangle(getX(), getY() + cumulativeHeight, 1,
					(f.getLineHeight() + 10) - 1);
			order.over = false;
			order.down = false;
			if ((t.width + 10) > maxWidth) {
				maxWidth = t.width + 10;
			}
			cumulativeHeight += f.getLineHeight() + 10;
		}
		for (final Order order : orders) {
			order.bounds.width = maxWidth;
		}
		setSize(maxWidth, cumulativeHeight);
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", PopupStyle.class);
	}

	@Override
	public void style(Skin skin, String styleName) {

		style = skin.get(styleName, PopupStyle.class);
	}

	@Override
	public void style(Style style) {

		if (PopupStyle.class.isAssignableFrom(style.getClass())) {
			this.style = (PopupStyle) style;
		}
	}
}
