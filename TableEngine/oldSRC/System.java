package org.engine;

import org.engine.Synchronizable.Animateable;
import org.engine.gui.PlainGUI;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.model.PlainModel;
import org.engine.object.MultiViewSynchronizable;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class System extends Layer implements Skinnable{

	
	
	public class Option {

		String name;
		String value;

		public Option(final String name, final String value) {

			this.name = name;
			this.value = value;
		}
	}

}

	class SystemGUI extends PlainGUI {

		

		private String actualInput = "";

		BlinkingThread blinkThread = new BlinkingThread();

		private String showInput = "";

		public SystemGUI() {

			super();
		}

		@Override
		public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

			if ((e.getType() == InputEvent.TYPE_INPUT_CHAR) && visible) {

				actualInput += e.getCharacter();
				blinkThread.update();
				Gdx.graphics.requestRendering();

				return true;
			}

			if ((e.getType() == InputEvent.TYPE_INPUT_KEY) && visible) {
				if (e.getCharacter() == InputEvent.KEY_ENTER) {

					t.setBlockIncomingNetworkTraffic(true);
					addChatMessage(parentUniverse.getPersonalPlayerName()
							+ " ~ " + actualInput);
					parentUniverse.getNetwork().fireUpdate();
					parentUniverse.setBlockIncomingNetworkTraffic(false);
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
		public void output(final Graphics g) {

			
//			// LOOK IF THIS PLAYER IS ALREADY IN THE LIST
//			if (!containsPlayer(parentUniverse.getPersonalPlayerID())) {
//
//				parentUniverse.setBusy(true);
//				addPlayer(parentUniverse.getPersonalPlayerID(),
//						parentUniverse.getPersonalPlayerName(),
//						MultiViewSynchronizable.VIEWER_SPECTATOR);
//				parentUniverse.getNetwork().fireUpdate();
//				parentUniverse.setBusy(false);
//
//			} else {
//				if (!parentUniverse.getPersonalPlayerName().equals(
//						getPlayer(parentUniverse.getPersonalPlayerID()).name)
//						|| !(parentUniverse.getPersonalPlayerView() == getPlayer(parentUniverse
//								.getPersonalPlayerID()).viewNo)) {
//
//					parentUniverse.setBusy(true);
//					parentUniverse
//							.setPersonalPlayerName(getPlayer(parentUniverse
//									.getPersonalPlayerID()).name);
//					parentUniverse.setPersonalView(getPlayer(parentUniverse
//							.getPersonalPlayerID()).viewNo);
//					parentUniverse.getNetwork().fireUpdate();
//					parentUniverse.setBusy(false);
//
//				}
//			}
//		}
	}

	class SystemModel extends PlainModel {

		@Override
		public Synchronizable[] addObjects(final String contentString) {

			final String[] s = contentString.split(Synchronizable.DELIMITER);
			final String[] chats = s[0].substring(1, s[0].length() - 1).split(
					Synchronizable.ENUM_DELIMITER);
			final String[] animations = s[1].substring(1, s[1].length() - 1)
					.split(Synchronizable.ENUM_DELIMITER);
			final String[] players = s[2].substring(1, s[2].length() - 1)
					.split(Synchronizable.ENUM_DELIMITER);
			playerList.clear();
			for (int i = 0; i < players.length; i++) {

				final Player p = new Player(players[i], players[i + 1],
						Integer.parseInt(players[i + 2]));
				playerList.add(p);
				i += 2;
			}
			final String[] bans = s[3].substring(0, s[3].length() - 1).split(
					Synchronizable.ENUM_DELIMITER);
			chatList = new Array<String>(chats);
			animationList = new Array<String>(animations);
			// playerList = new Array<Player>(players);
			// TODO convert to players.
			banList = new Array<String>(bans);
			return new Synchronizable[0];
		}

		@Override
		public String createLayerString() {

			// chat1,chat2;animation1,animation2;player1name,player1id,player1
			String s = "";
			s += chatList.toString() + Synchronizable.DELIMITER;
			s += animationList.toString() + Synchronizable.DELIMITER;
			s += playerList.toString() + Synchronizable.DELIMITER;
			s += banList.toString() + Synchronizable.DELIMITER;
			return s;
		}
	}


	public boolean visible;

	private ChatItemStyle style;

	System(final TableEngine t) {

		gui = new SystemGUI();
		model = new SystemModel();
		this.t = t;
		synchronize = true;
		label = "SYSTEM";
		style(t.uiSkin);
	}




	private void addAnimationToAlreadyPerfomedList(final String animation) {

		if (performedAnimations.getSize() < (System.ANIMATION_LIMIT * System.PLAYER_LIMIT)) {
			performedAnimations.add(animation);
		} else {
			for (int i = 1; i < performedAnimations.getSize(); i++) {
				performedAnimations.set(i - 1, performedAnimations.get(i));
			}
			performedAnimations.set(
					(System.ANIMATION_LIMIT * System.PLAYER_LIMIT) - 1,
					animation);
		}
	}

	public void addChatMessage(String message) {

		message = parentUniverse.getNetwork()
				.correctUserInputForNetworkIncompatibility(message);
		if (chatList.getSize() < System.CHAT_LIMIT) {
			chatList.add(message);
		} else {

			for (int i = 1; i < chatList.getSize(); i++) {
				chatList.set(i - 1, chatList.get(i));
			}
			chatList.set(System.CHAT_LIMIT - 1, message);
		}
	}

	public void addOption(final Option o) {

		optionList.add(o);
	}

	public boolean addPlayer(final String id, final String name, final int view) {

		if (!containsPlayer(id) && (playerList.getSize() < System.PLAYER_LIMIT)) {
			playerList.add(new Player(id, name, view));
			parentUniverse.getNetwork().fireUpdate();
			return true;
		}
		return false;
	}

	public void checkAnimations() {

		final Array<String> performAnimList = new Array<String>();
		performAnimList.addAll(animationList);
		final Array<String> deleteAnimBecauseWasAlreadyPerformedList = new Array<String>();
		for (final String oldAnim : performedAnimations) {

			for (final String newAnim : performAnimList) {

				if (oldAnim.equals(newAnim)) {
					deleteAnimBecauseWasAlreadyPerformedList.add(newAnim);
				}
			}
		}

		for (final String deleteMe : deleteAnimBecauseWasAlreadyPerformedList) {
			performAnimList.removeValue(deleteMe, false);
		}

		for (final String performMe : performAnimList) {

			for (final Synchronizable s : parentUniverse
					.getLayer(Universe.LayerBackGround).getModel()
					.getAllObjects()) {

				try {
					final Animateable a = (Animateable) s;
					a.animate(performMe);
				} catch (final Exception e) {

				}

			}

			addAnimationToAlreadyPerfomedList(performMe);

		}
	}

	public boolean containsOption(final String name) {

		boolean isInList = false;
		for (final Option o : optionList) {

			if (o.name.equals(name)) {

				isInList = true;

			}
		}
		return isInList;
	}

	public boolean containsPlayer(final String id) {

		boolean isInList = false;
		for (final Player p : playerList) {

			if (p.userID.equals(id)) {

				isInList = true;

			}
		}
		return isInList;
	}

	public Array<Option> getAllOptions() {

		return optionList;
	}

	public Array<Player> getAllPlayers() {

		return new Array<Player>(playerList);
	}

	public String getOption(final String name) {

		Option aim = null;
		for (final Option o : optionList) {

			if (o.name.equals(name)) {

				aim = o;
			}
		}
		return aim.value;
	}

	public Player getPlayer(final String id) {

		Player aim = null;
		for (final Player p : playerList) {

			if (p.userID.equals(id)) {

				aim = p;
			}
		}
		return aim;
	}

	public boolean removePlayer(final String id) {

		if (containsPlayer(id)) {
			return playerList.removeValue(getPlayer(id), true);
		} else {
			return false;
		}
	}

	public boolean setPlayer(final String id, final String name, final int view) {

		boolean isInList = false;
		for (final Player p : playerList) {

			if (p.userID.equals(id)) {

				isInList = true;
				if (!p.name.equals(name) || !(p.viewNo == view)) {
					p.name = name;
					p.viewNo = view;
					parentUniverse.getNetwork().fireUpdate();
				}

			}
		}
		return isInList;

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
		// TODO Auto-generated method stub
		
	}
}
