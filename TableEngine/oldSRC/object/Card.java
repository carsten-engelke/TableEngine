package org.engine.object;

import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.Synchronizable.Animateable;
import org.engine.System;
import org.engine.Universe;
import org.engine.geometry.Cube;
import org.engine.geometry.Vector3;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.resource.BasicResource;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;

/**
 * The Class Card. Represents a simple Card with 2 sides. To handle viewing of
 * either side it extends <link>MultiViewSynchronizable</link>
 */
public class Card extends MultiViewSynchronizable implements PopupSlave,
		Animateable {

	/** The is tapped or not?. */
	protected boolean tapped;

	protected Property tappedProp = new Property("TAP", "false");

	/**
	 * The value. Consists of the language, the value and the backside image
	 * e.g. "en.c1-blueback". This value must be lying in the database ("db"
	 * file in resources/basic) to be drawn correctly.
	 */
	protected String value;

	protected Property valueProp = new Property("CARD", "en.c1-blueback");

	/**
	 * Instantiates a new card.
	 */
	public Card() {

		sides = 2;
		value = valueProp.defaultContent;
		tapped = Boolean.parseBoolean(tappedProp.defaultContent);
		untransformedSize = new Vector3(142, 182, 1);
		setSize(untransformedSize);
		shiftGrid = 70;
	}

	/**
	 * Instantiates a new card.
	 * 
	 * @param c
	 *            the c
	 * @param uniqueID
	 *            the animation focusImage (used to allocate animations to this
	 *            specific card) must therefore be UNIQUE
	 * @param angle
	 *            the angle
	 * @param unrotatedSize
	 *            the original size (important for calculating turns and taps)
	 * @param playerView
	 *            the player view array (eg [0,1,0,0] -> only Player 2 sees side
	 *            1 other players see side 0)
	 * @param value
	 *            the value (usually "FRONTID-BACKID")
	 * @param tapped
	 *            whether card is tapped or not
	 */
	public Card(final Cube borders, final String uniqueID,
			final int gridSquare, final int angle,
			final ViewStorage playerView, final String value,
			final boolean tapped) {

		super(borders, uniqueID, gridSquare, angle, 90, playerView);
		sides = 2;
		this.value = value;
		this.tapped = tapped;
	}

	public String getValue() {

		return value;
	}

	@Override
	public void initialize(final Layer l) {

		super.initialize(l);
		abilities.add(BasicObject.MODE_SHIFT);
	}

	public boolean isTapped() {

		return tapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.object.MultiViewSynchronizable#paintView(java.awt.Graphics2D,
	 * int, java.awt.Rectangle)
	 */
	@Override
	protected void paintView(final Graphics g2d, final int i) {

		if (i != 0) {

			// DRAW FRONTSIDE = Part between "." and "-" in card value
			g2d.drawImage(
					getParentLayer()
							.getParentUniverse()
							.getResource(BasicResource.DESCRIPTION_ID)
							.getImage(
									"card/"
											+ getValue()
													.substring(
															getValue().indexOf(
																	".") + 1,
															getValue().indexOf(
																	"-"))), x,
					y, untransformedSize.x, untransformedSize.y);
		} else {

			// DRAW BACKSIDE = Part after "-" in card value
			g2d.drawImage(
					getParentLayer()
							.getParentUniverse()
							.getResource(BasicResource.DESCRIPTION_ID)
							.getImage(
									"card/"
											+ getValue()
													.substring(
															getValue().indexOf(
																	"-") + 1)),
					x, y, untransformedSize.x, untransformedSize.y);
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

		super.posClicked(e);
		if ((e.getCharacter() == InputEvent.CHAR_NORMAL_POS)
				&& (e.getTap_Amount() == 2)) {

			// Tap upon double left click.
			tap();

		}
		if (e.getCharacter() == InputEvent.CHAR_ALT_POS) {

			// Show menu upon right click containing the orders "Remove Card",
			// "Tap" and
			// "Change side"
			getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(true);
			final Popup popup = new Popup(new Array<String>(new String[] {
					parentUniverse.getText().getProperty("cardTap"),
					parentUniverse.getText().getProperty("cardSide"),
					parentUniverse.getText().getProperty("cardRemove") }), this);
			popup.setLocation(Gdx.input.getX(), Gdx.graphics.getHeight()
					- Gdx.input.getY(), Synchronizable.START_Z_VALUE);
			getParentLayer().getParentUniverse()
					.getLayer(Universe.LayerMenuPopUp).getModel()
					.addObject(popup);
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

		// Forward the currently showing card side to the magnifier object of
		// parent universe.
		if (getView() == 0) {
			getParentLayer()
					.getParentUniverse()
					.getMagnifier()
					.setFocussedObject(
							getValue().substring(getValue().indexOf("-") + 1));
		} else {
			getParentLayer()
					.getParentUniverse()
					.getMagnifier()
					.setFocussedObject(
							getValue().substring(getValue().indexOf(".") + 1,
									getValue().indexOf("-")));
		}
		return super.posEntered(e);
	}

	@Override
	public boolean posMoved(final InputEvent e) {

		// Forward the currently showing card side to the magnifier object of
		// parent universe.
		if (getView() == 0) {
			getParentLayer()
					.getParentUniverse()
					.getMagnifier()
					.setFocussedObject(
							getValue().substring(getValue().indexOf("-") + 1));
		} else {
			getParentLayer()
					.getParentUniverse()
					.getMagnifier()
					.setFocussedObject(
							getValue().substring(getValue().indexOf(".") + 1,
									getValue().indexOf("-")));
		}
		return super.posMoved(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.object.RotateSynchronizable#mouseReleased(java.awt.event
	 * .MouseEvent)
	 */
	@Override
	public boolean posReleased(final InputEvent e) {

		return super.posReleased(e);
	}

	@Override
	public void setProperties(final Array<Property> a) {

		super.setProperties(a);
		value = a.get(valueProp.id).content;
		tapped = Boolean.parseBoolean(a.get(tappedProp.id).content);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.gui.menu.PopupSlave#receiveOrder(java.lang.String)
	 */
	@Override
	public void receiveOrder(final String order) {

		if (order.equals(getParentLayer().getParentUniverse().getText()
				.getProperty("cardTap"))) {

			tap();
		}

		if (order.equals(getParentLayer().getParentUniverse().getText()
				.getProperty("cardSide"))) {

			final int playerNumber = getParentLayer().getParentUniverse()
					.getPersonalPlayerView();
			if ((playerNumber >= 0) && (playerNumber < System.PLAYER_LIMIT)) {
				getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(true);
				int newSide;

				if (getView() == 0) {
					newSide = 1;
				} else {
					newSide = 0;
				}
				newSide = setView(playerNumber, newSide);
				// getParentLayer()
				// .getParentUniverse()
				// .getSyncSystem()
				// .addChatMessage(
				// getParentLayer().getParentUniverse()
				// .getPersonalPlayerName()
				// + " "
				// + getParentLayer().getParentUniverse()
				// .getText()
				// .getProperty("cardSideChat")
				// + String.valueOf(newSide));
				getParentLayer().getParentUniverse().getNetwork().fireUpdate();
				getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(false);

			} else {

				// JOptionPane.showMessageDialog(getParentLayer().getParentUniverse(),
				// getParentLayer().getParentUniverse()
				// .getText()
				// .getProperty("cardPlayerOnly"));
			}

		}

		if (order.equals(getParentLayer().getParentUniverse().getText()
				.getProperty("cardRemove"))) {
			getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(true);
			getParentLayer().getParentUniverse()
					.getResource(BasicResource.DESCRIPTION_ID).getSound("draw")
					.play();
			getParentLayer().getModel().removeObject(this);
			getParentLayer().getParentUniverse().getNetwork().fireUpdate();
			getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(false);
		}

		if (order.equals(PopupSlave.CANCEL_ORDER)) {
			getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(false);
		}
	}

	public void setTapped(final boolean tapped) {

		this.tapped = tapped;
	}

	public void setValue(final String value) {

		this.value = value;
	}

	@Override
	public Array<Property> getProperties() {

		valueProp.content = value;
		tappedProp.content = String.valueOf(tapped);
		final Array<Property> a = super.getProperties();
		a.add(valueProp);
		a.add(tappedProp);
		return a;
	}

	/**
	 * Tap.
	 */
	private void tap() {

		getParentLayer().getParentUniverse().setBlockIncomingNetworkTraffic(true);
		final int angleBefore = angle;
		if (!isTapped()) {

			angle += 90;
			setTapped(true);
		} else {
			angle -= 90;
			setTapped(false);
		}
		parentUniverse.getSystem().addAnimation(
				"rotate(" + angleBefore + ":" + angle + ")");
		Gdx.graphics.requestRendering();
	}

}
