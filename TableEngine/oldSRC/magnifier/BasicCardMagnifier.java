package org.engine.magnifier;

import org.engine.gui.output.Graphics;
import org.engine.object.BasicObject;
import org.engine.object.Card;

import com.badlogic.gdx.Gdx;

/**
 * The Class BasicCardMagnifier.
 */
public class BasicCardMagnifier extends BasicObject implements
		Magnifier {

	public static final float NOT_VISIBLE = 0.0F;

	/** The value. */
	String focusImage = null;

	private float multiplier = 1.0F;

	private float origHeight = 0;

	private float origWidth = 0;

	public BasicCardMagnifier() {

		super();
		// Class to show is Card -> Set Size according to a standard card:
		final Card c = new Card();
		setSize(c.untransformedSize.x, c.untransformedSize.y, 0);
		abilities.add(BasicObject.MODE_SHIFT);
		origWidth = getWidth();
		origHeight = getHeight();
		setLocation(0, (Gdx.graphics.getHeight() / 2) + (getHeight() / 2), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Magnifier#getFocussedObject()
	 */
	@Override
	public Object getFocussedObject() {

		return focusImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Magnifier#isMagnificating()
	 */
	@Override
	public float isMagnificating() {

		return multiplier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	@Override
	public void paintTransformed(final Graphics g) {

		if (getFocussedObject() == null) {
			setMagnificating(BasicCardMagnifier.NOT_VISIBLE);
		} else {
			g.drawImage(
					getParentLayer().getParentUniverse().getResource("Basic")
							.getImage(focusImage), getX(), getY(), getWidth(),
					getHeight());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Magnifier#setFocussedObject(java.lang.String)
	 */
	@Override
	public void setFocussedObject(final String focusImage) {

		if (this.focusImage != focusImage) {

			this.focusImage = focusImage;
			setSize(getParentLayer().getParentUniverse().getResource("Basic")
					.getImage(focusImage).getRegionWidth()
					* multiplier, getParentLayer().getParentUniverse()
					.getResource("Basic").getImage(focusImage)
					.getRegionHeight()
					* multiplier, 0);
			Gdx.graphics.requestRendering();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Magnifier#setMagnificating(boolean)
	 */
	@Override
	public void setMagnificating(final float mul) {

		multiplier = mul;
		setWidth(origWidth * mul);
		setHeight(origHeight * mul);
	}
}