package org.engine.menu;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.TableEngine;
import org.engine.geometry.Cube;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.utils.Array;
import org.engine.utils.ArrayMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Menu extends Cube implements Interactable, Skinnable {

	public static class MenuStyle extends Style {

		public Drawable background, expand, up, down, over, imageLeft,
				imageRight, imageSettings;

		public MenuStyle() {

		}

		public MenuStyle(final Drawable background, final Drawable expand,
				final Drawable up, final Drawable down, final Drawable over,
				final Drawable imageLeft, final Drawable imageRight,
				final Drawable imageSettings) {

			this.background = background;
			this.expand = expand;
			this.up = up;
			this.down = down;
			this.over = over;
			this.imageLeft = imageLeft;
			this.imageRight = imageRight;
			this.imageSettings = imageSettings;
		}

		public MenuStyle(final MenuStyle style) {

			background = style.background;
			expand = style.expand;
			up = style.up;
			down = style.down;
			over = style.over;
			imageLeft = style.imageLeft;
			imageRight = style.imageRight;
			imageSettings = style.imageSettings;
		}
	}

	public static final String COMPASS_ITEM = "COMPASS";

	public static final String CHAT_ITEM = "CHAT";

	public static final String TIMELINE_ITEM = "TIME";

	private int item = 0;

	private final boolean[] isDown = new boolean[3];

	private final boolean[] isOver = new boolean[3];

	private boolean isPressing;

	private Layer l;

	private TableEngine t;

	private Array<MenuItem> menuItemList = new Array<MenuItem>();

	private Stage settings;

	private boolean showButtons;

	private MenuStyle style;

	public Menu() {

		super();
	}

	private void checkClick(final InputEvent e) {

		isOver[0] = false;
		isOver[1] = false;
		isOver[2] = false;
		isDown[0] = false;
		isDown[1] = false;
		isDown[2] = false;

		if (e.getY() > ((getHeight() / 3) * 2)) {
			setNextIcon();
		}

		if ((e.getY() > (getHeight() / 3))
				&& (e.getY() <= ((getHeight() / 3) * 2))) {
			setLastIcon();
		}

		if (e.getY() <= (getHeight() / 3)) {
			showSettings();
		}
	}

	private void checkDown(final InputEvent e) {

		isOver[0] = false;
		isOver[1] = false;
		isOver[2] = false;
		isDown[0] = false;
		isDown[1] = false;
		isDown[2] = false;

		isDown[0] = true;
		if (e.getY() > (getHeight() / 3)) {
			isDown[0] = false;
			isDown[1] = true;
		}
		if (e.getY() > ((getHeight() / 3) * 2)) {
			isDown[1] = false;
			isDown[2] = true;
		}
	}

	private void checkOver(final InputEvent e) {

		isOver[0] = false;
		isOver[1] = false;
		isOver[2] = false;
		isDown[0] = false;
		isDown[1] = false;
		isDown[2] = false;

		isOver[0] = true;
		if (e.getY() > (getHeight() / 3)) {
			isOver[0] = false;
			isOver[1] = true;
		}
		if (e.getY() > ((getHeight() / 3) * 2)) {
			isOver[1] = false;
			isOver[2] = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#getBounds()
	 */
	@Override
	public Cube getBounds() {

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#getParentLayer()
	 */
	@Override
	public Layer getParentLayer() {

		return l;
	}

	public Stage getSettings() {
		return settings;
	}

	private void hideAll() {

		for (MenuItem mi : menuItemList) {

			mi.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#initialize(org.engine.Layer)
	 */
	@Override
	public void initialize(final Layer layer) {

		l = layer;
		t = layer.t;
		style(t.uiSkin, t.uiStyleName);

		// Load Button to hide/show menu
		adaptToScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Load Compass
		CompassItem ci = new CompassItem();
		menuItemList.add(ci);
		l.addInteractable(ci);

		// Load Chat
		
		ChatItem chi = new ChatItem();
		menuItemList.add(chi);
		l.addInteractable(chi);
		
		// Load TimeMachine
		TimeLineItem tli = new TimeLineItem();
		menuItemList.add(tli);
		l.addInteractable(tli);

		isOver[0] = false;
		isOver[1] = false;
		isOver[2] = false;

		isDown[0] = false;
		isDown[1] = false;
		isDown[2] = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#input(java.awt.event.MouseEvent,
	 * java.awt.event.KeyEvent, java.awt.event.MouseWheelEvent, boolean)
	 */
	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		if (getRectangle2D().contains(e.getX(), e.getY())) {

			if (e.getType() == InputEvent.TYPE_MOVE_POS) {

				isOver[0] = true;
				isDown[0] = false;
				if (showButtons) {
					checkOver(e);
				}

			}
			if (e.getType() == InputEvent.TYPE_DRAG_POS) {

				if (isPressing) {
					isDown[0] = true;
					if (showButtons) {
						checkDown(e);
					}
				}
			}
			if (e.getType() == InputEvent.TYPE_CLICK_POS) {

				if (!showButtons) {
					showButtons(true);
				} else {
					checkClick(e);
				}
				return true;
			}
			if (e.getType() == InputEvent.TYPE_PRESS_POS) {

				isOver[0] = false;
				isDown[0] = true;
				if (showButtons) {
					checkDown(e);
					isPressing = true;
				}
			}
			if (e.getType() == InputEvent.TYPE_RELEASE_POS) {

				isOver[0] = false;
				isOver[1] = false;
				isOver[2] = false;
				isDown[0] = false;
				isDown[1] = false;
				isDown[2] = false;
			}
			return true;
		} else {
			if ((e.getType() >= InputEvent.TYPE_POS_MIN)
					&& (e.getType() <= InputEvent.TYPE_POS_MAX)) {
				isOver[0] = false;
				isOver[1] = false;
				isOver[2] = false;
				isDown[0] = false;
				isDown[1] = false;
				isDown[2] = false;
			}
			if ((e.getType() == InputEvent.TYPE_CLICK_POS)
					&& (e.getTap_Amount() > 1)) {

				showButtons(false);
			}
		}

		return false;
	}

	@Override
	public void output(final Graphics g) {

		g.drawUI(style.background, getX(), getY(), getWidth(), getHeight());
		if (!showButtons) {
			if (isOver[0]) {
				g.drawUI(style.over, getX(), getY(), getWidth(), getHeight());
			} else {
				if (isDown[0]) {
					g.drawUI(style.down, getX(), getY(), getWidth(),
							getHeight());
				} else {
					g.drawUI(style.up, getX(), getY(), getWidth(), getHeight());
				}
			}

			float h = style.expand.getMinHeight() / style.expand.getMinWidth()
					* getWidth();
			g.drawUI(style.expand, getX(),
					(getY() + (getHeight() / 2)) - h / 2, getWidth(), h);

		} else {
			if (isOver[0]) {
				g.drawUI(style.over, getX(), getY(), getWidth(),
						getHeight() / 3);
			} else {
				if (isDown[0]) {
					g.drawUI(style.down, getX(), getY(), getWidth(),
							getHeight() / 3);
				} else {
					g.drawUI(style.up, getX(), getY(), getWidth(),
							getHeight() / 3);
				}
			}
			if (isOver[1]) {
				g.drawUI(style.over, getX(), getY() + (getHeight() / 3),
						getWidth(), getHeight() / 3);
			} else {
				if (isDown[1]) {
					g.drawUI(style.down, getX(), getY() + (getHeight() / 3),
							getWidth(), getHeight() / 3);
				} else {
					g.drawUI(style.up, getX(), getY() + (getHeight() / 3),
							getWidth(), getHeight() / 3);
				}
			}
			if (isOver[2]) {
				g.drawUI(style.over, getX(), getY() + ((getHeight() / 3) * 2),
						getWidth(), getHeight() / 3);
			} else {
				if (isDown[2]) {
					g.drawUI(style.down, getX(), getY()
							+ ((getHeight() / 3) * 2), getWidth(),
							getHeight() / 3);
				} else {
					g.drawUI(style.up, getX(),
							getY() + ((getHeight() / 3) * 2), getWidth(),
							getHeight() / 3);
				}
			}
			float h = style.imageSettings.getMinHeight()
					/ style.imageSettings.getMinWidth() * getWidth();
			g.drawUI(style.imageSettings, getX(), getY()
					+ (getHeight() / 3 / 2) - h / 2, getWidth(), h);
			h = style.imageLeft.getMinHeight() / style.imageLeft.getMinWidth()
					* getWidth();
			g.drawUI(style.imageLeft, getX(), getY() + (getHeight() / 3 / 2)
					+ (getHeight() / 3) - h / 2, getWidth(), h);
			h = style.imageRight.getMinHeight()
					/ style.imageRight.getMinWidth() * getWidth();
			g.drawUI(style.imageRight, getX(), getY() + (getHeight() / 3 / 2)
					+ ((getHeight() / 3) * 2) - h / 2, getWidth(), h);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#setBounds(org.engine.geometry.Cube)
	 */
	@Override
	public void setBounds(final Cube bounds) {

		setLocation(bounds.getLocation());
		setSize(bounds.getSize());
	}

	private void setLastIcon() {

		if (item > 0) {
			item--;
		} else {
			item = menuItemList.getSize() - 1;
		}
		showItem(menuItemList.get(item));
	}

	private void setNextIcon() {

		if (item < menuItemList.getSize() - 1) {
			item++;
		} else {
			item = 0;
		}
		showItem(menuItemList.get(item));
	}

	private void showItem(MenuItem mi) {
		
		for (MenuItem mItem : menuItemList) {
			
			if (mItem != mi) {
				mItem.setVisible(false);
			}
		}
		mi.setVisible(true);
	}
	
	public void showButtons(final boolean b) {

		showButtons = b;
		if (showButtons) {
			setSize(org.engine.utils.MathUtils.max(
					style.imageSettings.getMinWidth(),
					style.imageLeft.getMinWidth(),
					style.imageRight.getMinWidth()), getHeight(), getDepth());
			item = 0;
			showItem(menuItemList.get(item));
		} else {
			hideAll();
			setSize(style.expand.getMinWidth(), getHeight(), getDepth());
		}
	}


	private void showSettings() {

		hideAll();
		t.setBlockIncomingNetworkTraffic(true);
		t.setUI(t.settingsScreen, true);
		showButtons(false);
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", MenuStyle.class);
		setSize(style.expand.getMinWidth(), getHeight(), getDepth());
	}

	@Override
	public void style(Skin skin, String style) {

		this.style = skin.get(style, MenuStyle.class);
		setSize(this.style.expand.getMinWidth(), getHeight(), getDepth());
	}

	@Override
	public void style(Style style) {

		if (MenuStyle.class.isAssignableFrom(style.getClass())) {
			this.style = (MenuStyle) style;
			setSize(this.style.expand.getMinWidth(), getHeight(), getDepth());
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		setLocation(0, 0, 0);
		setSize(getWidth(), height, 0);
	}

	public void addMenuItems(Array<MenuItem> menuItems) {
		
		menuItemList.addAll(menuItems);
		ArrayMap<MenuItem, Integer> sortMap = new ArrayMap<MenuItem, Integer>();
		for (MenuItem mi : menuItemList) {
			sortMap.put(mi, mi.getPreferredPosition());
		}
		sortMap.sortToValues();
		menuItemList.clear();
		menuItemList.addAll(sortMap.keys());
	}

	public MenuItem getMenuItem(String label) {
		for (MenuItem mi : menuItemList) {
			if (mi.getLabel().equals(label)) {
				return mi;
			}
		}
		return null;
	}

}
