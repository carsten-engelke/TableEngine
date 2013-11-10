package org.engine.menu;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.TableEngine;
import org.engine.geometry.Cube;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChatItem implements MenuItem, Skinnable, Interactable {

	private Cube bounds = new Cube();
	private Layer l;
	private TableEngine t;
	private boolean visible = false;
	private String actualInput = "";
	private String showInput = "";
	private BlinkingThread blinkThread = null;
	private ChatItemStyle style;

	@Override
	public Cube getBounds() {

		return bounds;
	}

	@Override
	public Layer getParentLayer() {

		return l;
	}

	@Override
	public void initialize(Layer layer) {

		this.l = layer;
		t = layer.t;
		blinkThread = new BlinkingThread();
		style(t.uiSkin, t.uiStyleName);
	}

	@Override
	public boolean input(InputEvent e, boolean wasCatchedAbove) {

		if ((e.getType() == InputEvent.TYPE_INPUT_CHAR) && visible) {

			actualInput += e.getCharacter();
			blinkThread.update();
			Gdx.graphics.requestRendering();

			return true;
		}

		if ((e.getType() == InputEvent.TYPE_INPUT_KEY) && visible) {
			if (e.getCharacter() == InputEvent.KEY_ENTER) {

				t.setBlockIncomingNetworkTraffic(true);
				t.chatList.get().add(t.getPrefString("playerName")
						+ " ~ " + actualInput);
				t.requestSend();
				t.setBlockIncomingNetworkTraffic(false);
				actualInput = "";
				blinkThread.update();
				Gdx.graphics.requestRendering();
				return true;
			}
			if (e.getCharacter() == InputEvent.KEY_BACKSPACE) {
				if (actualInput.length() > 0) {
					actualInput = actualInput.substring(0,
							actualInput.length() - 1);
				}
				blinkThread.update();
				Gdx.graphics.requestRendering();
				return true;
			}
		}
		return false;
	}

	@Override
	public void output(Graphics g) {

		g.setFont(style.font);
		if (t.getPrefBoolean("showChat")) {
			
			for (int line = 0; line < t.chatList.get().getSize(); line++) {

				if (t.chatList.get().get(line).length() > 0) {
					g.setColor(Color.WHITE);
					g.drawString(
							t.chatList.get().get(line),
							t.menu.width + 5,
							Gdx.graphics.getHeight()
									- 5
									- (g.getFont().getLineHeight() * (line + 1)));
				}

			}
		}
		if (visible) {

			if (blinkThread == null || !blinkThread.isAlive()) {
				blinkThread = new BlinkingThread();
				blinkThread.start();
			}
			if ((showInput != null) && (showInput.length() > 0)) {
				g.setColor(Color.WHITE);
				g.drawString(showInput, t.menu.width + 7,
						10);
			}
		}
	}

	@Override
	public void setBounds(Cube bounds) {

		this.bounds.setBounds(bounds);
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", ChatItemStyle.class);
	}

	@Override
	public void style(Skin skin, String style) {

		this.style = skin.get(style, ChatItemStyle.class);
	}

	@Override
	public void style(Style style) {

		if (ChatItemStyle.class.isAssignableFrom(style.getClass())) {
			
			this.style = (ChatItemStyle) style;
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

		bounds.setLocation(t.menu.width + 7, 10, 0);
		bounds.setSize(width - t.menu.width - 7, style.font.getLineHeight(), 1);
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

		return 3;
	}

	@Override
	public String getLabel() {

		return Menu.CHAT_ITEM;
	}

	public static class ChatItemStyle extends Style {

		public BitmapFont font;

		ChatItemStyle() {

		}

		ChatItemStyle(BitmapFont font) {

			this.font = font;
		}

		ChatItemStyle(ChatItemStyle style) {

			this.font = style.font;
		}
	}
	
	class BlinkingThread extends Thread {

		boolean appending = true;

		@Override
		public void run() {

			while (visible) {
				if (!appending) {
					showInput = actualInput + "|";
					appending = true;
				} else {
					showInput = actualInput;
					appending = false;
				}
				Gdx.graphics.requestRendering();
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			appending = false;
		}

		void update() {
			if (appending) {
				showInput = actualInput + "|";
			} else {
				showInput = actualInput;
			}
		}
	}
}
