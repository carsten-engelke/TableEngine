package org.engine.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.Synchronizable;
import org.engine.TableEngine;
import org.engine.geometry.Rectangle;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.language.BasicDefaultLanguage;
import org.engine.network.Server;
import org.engine.object.BasicObject;
import org.engine.object.popup.PopupSlave;
import org.engine.property.Information;
import org.engine.property.InformationArrayStringException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * The Class TimeMachine.
 */
public class TimeLineItem extends BasicObject implements PopupSlave,
		Skinnable, MenuItem {

	public static final int iconID = 3;

	private String caption = "";

	float CursorX = 0;

	private TimeLineItemStyle style;

	String reset = "";

	private final ArrayMap<Long, String> timeline = new ArrayMap<Long, String>(
			true, 1);

	public boolean visible = false;
	private Rectangle iconArea = new Rectangle();

	/**
	 * Adds the time point.
	 * 
	 * @param syncString
	 *            the sync string
	 */
	public void addTimePoint(final String syncString) {

		timeline.put(TimeUtils.millis(), syncString);

	}

	/**
	 * Call latest.
	 */
	public void callLatest() {

		t.setBlockIncomingNetworkTraffic(true);
		setTimePoint(timeline.getKeyAt(timeline.size - 1));
		t.requestSend();
		t.setBlockIncomingNetworkTraffic(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#initialize(org.engine.Layer)
	 */
	@Override
	public void initialize(final Layer l) {

		super.initialize(l);
		CursorX = getX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#input(java.awt.event.MouseEvent,
	 * java.awt.event.KeyEvent, java.awt.event.MouseWheelEvent, boolean)
	 */
	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		if (visible) {
			wasCatchedByMe = super.input(e, wasCatchedAbove);
		}
		return wasCatchedByMe;
	}

	/**
	 * Load from file.
	 * 
	 * @param load
	 *            the load
	 * @return true, if successful
	 */
	public boolean loadFromFile(final FileHandle load) {

		try {
			final BufferedReader br = load.reader(10000);
			timeline.clear();
			String s = br.readLine();
			while ((s != null) && (s != "")) {
				final long timestamp = Long.parseLong(s.substring(0,
						s.indexOf(Server.SERVER_START)));
				final String content = s.substring(
						s.indexOf(Server.SERVER_START)
								+ Server.SERVER_START.length(),
						s.indexOf(Server.SERVER_END));
				timeline.put(timestamp, content);
				s = br.readLine();
			}
			br.close();
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.object.DragDropSynchronizable#output(java.awt.Graphics2D,
	 * java.awt.Rectangle)
	 */
	@Override
	public void output(final Graphics g2d) {

		if (visible) {

			// draw Icon
			g2d.setColor(Color.WHITE);
			g2d.drawUI(style.background, iconArea.x, iconArea.y,
					iconArea.width, iconArea.height);
			g2d.drawUI(style.up, iconArea.x, iconArea.y, iconArea.width,
					iconArea.height);
			g2d.drawUI(style.icon, iconArea.x, iconArea.y, iconArea.width,
					iconArea.height);

			// draw TimeLine Bar background
			g2d.setColor(Color.WHITE);
			g2d.fillRect(getX() - 2, getY() - 2, getWidth() + 4,
					getHeight() + 4);
			// g2d.fillRect(CursorX - 3, getY() - 25,
			// g2d.getFont().getBounds(caption).width + 4, g2d.getFont()
			// .getBounds(caption).height + 4);

			// draw Cursor
			g2d.setFont(style.font);
			g2d.fillRect(CursorX - 2, getY() - 5, 9, 35);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(getX(), getY(), getWidth(), getHeight());
			g2d.drawString(caption, CursorX + 5, (getY() - 25)
					+ g2d.getFont().getBounds(caption).height + 2);

			// draw TimeLine Bar foreground
			g2d.fillRect(CursorX, getY() - 3, 5, 31);
			g2d.setColor(Color.WHITE);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#mouseClicked(java.awt.event.
	 * MouseEvent )
	 */
	@Override
	public boolean posClicked(final InputEvent e) {

		t.setUI(DialogFactory.createQuestionDialog(
				t.getText(BasicDefaultLanguage.timemachineAsk) + caption + "?",
				t.getText(BasicDefaultLanguage.yes),
				t.getText(BasicDefaultLanguage.no), t.uiSkin, this, t),
				TableEngine.SHOW_UI);
		return true;
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

		t.setBlockIncomingNetworkTraffic(true);
		super.posDragged(e);
		CursorX = e.getPosition().x - 1;
		if (CursorX < getX()) {
			CursorX = getX();
		}
		if (CursorX > (getX() + getWidth())) {
			CursorX = getX() + getWidth();
		}
		if (timeline.size > 0) {
			setTimePointCursor();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#mouseEntered(java.awt.event.
	 * MouseEvent )
	 */
	@Override
	public boolean posEntered(final InputEvent e) {

		t.setBlockIncomingNetworkTraffic(true);
		setReset();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.object.DragDropSynchronizable#mouseExited(java.awt.event.
	 * MouseEvent )
	 */
	@Override
	public boolean posExited(final InputEvent e) {

		t.getLayer(TableEngine.OBJECT_LAYER).clear();
		try {
			((Synchronizable) t.getLayer(TableEngine.OBJECT_LAYER))
					.setPropertiesFromInformation(Information.StringToInformations(reset));
		} catch (InformationArrayStringException e1) {
			e1.printStackTrace();
		}
		t.setBlockIncomingNetworkTraffic(false);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.DragDropSynchronizable#mouseMoved(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public boolean posMoved(final InputEvent e) {

		return posDragged(e);
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

		if (shifting) {

			// Ask player in a dialog if he wants to switch so selected time
			// point
			// Create a dialog Stage apply it to the TableEngine, switch to UI
			// mode afterwards
			t.setUI(DialogFactory.createQuestionDialog(
					t.getText(BasicDefaultLanguage.timemachineAsk) + caption
							+ "?", t.getText(BasicDefaultLanguage.yes),
					t.getText(BasicDefaultLanguage.no), t.uiSkin, this, t),
					TableEngine.SHOW_UI);
			shifting = false;
		}
		return false;
	}

	@Override
	public void receiveOrder(final String order) {
		if (order.equals(t.getText(BasicDefaultLanguage.yes))) {
			setTimePointCursor();
			t.requestSend();
			;
			t.menu.showButtons(false);
		}
		if (order.equals(t.getText(BasicDefaultLanguage.no))) {
			CursorX = getX();
			t.getLayer(TableEngine.OBJECT_LAYER).clear();
			try {
				((Synchronizable) t.getLayer(TableEngine.OBJECT_LAYER))
						.setPropertiesFromInformation(Information.StringToInformations(reset));
			} catch (InformationArrayStringException e1) {
				e1.printStackTrace();
			}
		}
		t.setBlockIncomingNetworkTraffic(false);
	}

	/**
	 * Save to file.
	 * 
	 * @param save
	 *            the save
	 */
	public void saveToFile(final FileHandle save) {

		try {
			final Writer w = save.writer(false);
			for (final long timestamp : timeline.keys()) {

				w.write(timestamp + Server.SERVER_START
						+ timeline.get(timestamp) + Server.SERVER_END + "\n");
			}
			w.close();

		} catch (final IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Sets the reset.
	 */
	private void setReset() {

		reset = Information.PropertiesToString(((Synchronizable) t
				.getLayer(TableEngine.OBJECT_LAYER)).getProperties());
	}

	/**
	 * Sets the time point.
	 * 
	 * @param index
	 *            the index
	 * @return the int
	 */
	private int setTimePoint(final int index) {

		caption = String.valueOf(index);
		final long time = timeline.getKeyAt(index);
		setTimePoint(time);
		return index;
	}

	/**
	 * Sets the time point.
	 * 
	 * @param time
	 *            the time
	 * @return the long
	 */
	private long setTimePoint(final long time) {

		final String s = timeline.get(time);
		t.getLayer(TableEngine.OBJECT_LAYER).clear();
		try {
			((Synchronizable) t.getLayer(TableEngine.OBJECT_LAYER))
					.setPropertiesFromInformation(Information.StringToInformations(s));
		} catch (InformationArrayStringException e1) {
			e1.printStackTrace();
		}
		return time;
	}

	/**
	 * Sets the time point cursor.
	 * 
	 * @return the int
	 */
	private int setTimePointCursor() {

		final int index = (int) Math
				.floor(((double) (CursorX - getX()) / (double) (getWidth()))
						* timeline.size);
		return setTimePoint(index);
	}

	public static class TimeLineItemStyle extends Style {

		public Drawable icon, background, up;

		public BitmapFont font;

		public TimeLineItemStyle() {

		}

		public TimeLineItemStyle(TimeLineItemStyle style) {

			this.icon = style.icon;
			this.background = style.background;
			this.up = style.up;
			this.font = style.font;
		}
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", TimeLineItemStyle.class);
	}

	@Override
	public void style(Skin skin, String style) {

		this.style = skin.get(style, TimeLineItemStyle.class);
	}

	@Override
	public void style(Style style) {

		if (TimeLineItemStyle.class.isAssignableFrom(style.getClass())) {
			this.style = (TimeLineItemStyle) style;
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		setY(40);
		setX(t.menu.width + 10);
		untransformedSize.get().x = width - x - 20;
		untransformedSize.get().y = 25;
		setSize(untransformedSize.get());
		setZ(0);

		iconArea.set(MathUtils.ceil(width / 2 - style.icon.getMinWidth() / 2),
				MathUtils.ceil(height / 2 - style.icon.getMinHeight() / 2),
				style.icon.getMinWidth(), style.icon.getMinHeight());
	}

	@Override
	public void setVisible(boolean b) {

		this.visible = b;
	}

	@Override
	public boolean isVisible() {
		
		return visible;
	}

	@Override
	public int getPreferredPosition() {

		return 10;
	}

	@Override
	public String getLabel() {

		return Menu.TIMELINE_ITEM;
	}
}
