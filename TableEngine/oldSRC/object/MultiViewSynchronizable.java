/*
 * 
 */
package org.engine.object;

import org.engine.Synchronizable;
import org.engine.System;
import org.engine.geometry.Cube;
import org.engine.gui.output.Graphics;
import org.engine.utils.Array;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * The Class MultiViewSynchronizable.
 */
public class MultiViewSynchronizable extends BasicObject {

	public enum Viewer {

		Player, Spectator, Supervisor;
	}

	class ViewOutOfRangeException extends Exception {

		/**
     * 
     */
		private static final long serialVersionUID = 1L;

		int lowerLimit = 0;
		int upperLimit = 0;
		int value = 0;

		public ViewOutOfRangeException(final int lowerLimit,
				final int upperLimit, final int value) {

			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			this.value = value;

		}

		@Override
		public String getMessage() {

			return "Value out of Range: " + value + "[" + lowerLimit + " - "
					+ upperLimit + "]";
		}
	}

	/**
	 * The Class View. It stores a number for each player in the game. For
	 * instance: A card has two sides, back and front. There are 5 players in
	 * the game of which only player 2 and 5 may see the front side and the
	 * others may not see it. An Integer of 0 would mean you see the backside
	 * and an Integer of 1 the front side. The corresponding View object would
	 * store an int[] of {0,1,0,0,1} and return the view number when a player
	 * asks for his/her view.
	 */
	public class ViewStorage {

		private int[] storage;

		// The number that is returned when the view for SUPERVISOR is
		// demanded
		// (default: 1)
		private int viewEverything;

		// The number that is returned when the view for SPECTATOR is
		// demanded
		// (default: 0)
		private int viewNothing;

		public ViewStorage() {

			viewEverything = 1;
			viewNothing = 0;
			setStorageTo(viewNothing);
		}

		public ViewStorage(final int viewEverything, final int viewNothing) {

			this.viewEverything = viewEverything;
			this.viewNothing = viewNothing;
			setStorageTo(viewNothing);
		}

		public ViewStorage(final String s) {

			this();
			stringToViewStorage(s);
		}

		private int checkRangeOfView(final int view) {

			if ((view < 0) || (view >= (sides))) {
				return viewNothing;
			}
			return view;
		}

		public int getView(final Viewer v, final int number) {

			int view = 0;
			if (v == Viewer.Spectator) {
				view = viewNothing;
			}
			if (v == Viewer.Supervisor) {
				view = viewEverything;
			}
			if (v == Viewer.Player) {

				if ((number < 0) || (number > storage.length)) {

					view = viewNothing;

				} else {

					view = storage[number];

				}

				return checkRangeOfView(view);

			}
			return view;
		}

		private void setStorageTo(final int newView) {

			storage = new int[System.PLAYER_LIMIT];
			for (int i = 0; i < storage.length; i++) {
				storage[i] = newView;
			}
		}

		public int setView(final int playerNumber, final int newViewNumber) {

			if ((playerNumber < 0) || (playerNumber > storage.length)) {

				return viewNothing;

			} else {

				storage[playerNumber] = checkRangeOfView(newViewNumber);
				return storage[playerNumber];
			}
		}

		public void setViewEverything(final int viewNumber) {

			viewEverything = viewNumber;
		}

		public void setViewNothing(final int viewNumber) {

			viewNothing = viewNumber;
		}

		public void stringToViewStorage(String s) {

			s = s.substring(1, s.length() - 1);
			final String[] split = s.split(Synchronizable.ENUM_DELIMITER);
			for (int i = 0; i < split.length; i++) {
				storage[i] = Integer.parseInt(split[i]);
			}
		}

		public String viewStorageToString() {

			String s = Synchronizable.ENUM_START;
			for (final int i : storage) {
				s += i + Synchronizable.ENUM_DELIMITER;
			}
			s = s.substring(0, s.length() - 1);
			s += Synchronizable.ENUM_END;
			return s;
		}

	}

	/** The Constant VIEWER_SPECTATOR. */
	public static final int VIEWER_SPECTATOR = 0;

	/** The Constant VIEWER_SUPERVISOR. */
	public static final int VIEWER_SUPERVISOR = -1;

	/** The number of sides. */
	protected int sides = 2;

	protected Property view = new Property("VIEW",
			"[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]");

	protected ViewStorage viewStorage;

	/**
	 * Instantiates a new multi view synchronizable.
	 */
	public MultiViewSynchronizable() {

		// Create one view for each possible viewer
		viewStorage = new ViewStorage();
	}

	/**
	 * Instantiates a new multi view synchronizable.
	 * 
	 * @param c
	 *            the c
	 * @param uniqueID
	 *            the anim focusImage
	 * @param angle
	 *            the angle
	 * @param unrotatedSize
	 *            the original size
	 * @param playerView
	 *            the player view
	 */
	public MultiViewSynchronizable(final Cube c, final String uniqueID,
			final int dragGrid, final int angle, final int angleGrid,
			final ViewStorage storage) {

		super(c, uniqueID, dragGrid, angle, angleGrid);
		viewStorage = storage;
	}

	/**
	 * Gets the view.
	 * 
	 * @return the view
	 */
	public int getView() {

		final int viewerNo = parentUniverse.getPersonalPlayerView();
		return viewStorage.getView(Viewer.Player, viewerNo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.object.RotateSynchronizable#paint(java.awt.Graphics2D,
	 * java.awt.Rectangle)
	 */
	@Override
	protected void paintTransformed(final Graphics g2d) {

		paintView(g2d, getView());
	}

	/**
	 * Paint view.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param i
	 *            the i
	 * @param r
	 *            the r
	 */
	protected void paintView(final Graphics g2d, final int i) {

		super.paintTransformed(g2d);
		g2d.drawString(
				"VIEW:" + i,
				(int) ((getX() + (untransformedSize.x / 2)) - (g2d.getFont()
						.getBounds("VIEW:" + i).width / 2)),
				(int) (getY() + (2 * g2d.getFont().getBounds("VIEW:" + i).height)));
	}

	@Override
	public void setProperties(final Array<Property> a) {

		super.setProperties(a);
		viewStorage.stringToViewStorage(a.get(view.id).content);
	}

	public int setView(final int playerNumber, final int newViewNumber) {

		return viewStorage.setView(playerNumber, newViewNumber);
	}

	@Override
	public Array<Property> getProperties() {

		final Array<Property> a = super.getProperties();
		view.content = viewStorage.viewStorageToString();
		a.add(view);
		return a;
	}
}
