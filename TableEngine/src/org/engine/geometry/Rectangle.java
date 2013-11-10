package org.engine.geometry;

import org.engine.utils.Array;

public class Rectangle extends com.badlogic.gdx.math.Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Rectangle() {
		super();
	}

	public Rectangle(final float x, final float y, final float width, final float height) {
		super(x, y, width, height);
	}

	@Override
	public String toString() {

		return Array.ENUM_START + x + Array.ENUM_DELIMITER
				+ y + Array.ENUM_DELIMITER + width
				+ Array.ENUM_DELIMITER + height
				+ Array.ENUM_END;
	}
}
