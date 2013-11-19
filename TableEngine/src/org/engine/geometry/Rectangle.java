package org.engine.geometry;

import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

public class Rectangle extends com.badlogic.gdx.math.Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public Rectangle() {
		super();
	}

	public Rectangle(final float x, final float y, final float width, final float height) {
		super(x, y, width, height);
	}
	
	@Override
	public String toString() {

		return super.toString() + ":" + asString();
	}
	
	public String asString() {
		
		return Array.ENUM_START + x + Array.ENUM_DELIMITER
		+ y + Array.ENUM_DELIMITER + width
		+ Array.ENUM_DELIMITER + height
		+ Array.ENUM_END;
	}
	
	public void applyString(String s) {
		
		try {
			s = s.substring(Array.ENUM_START.length(), s.indexOf(Array.ENUM_END));
			String[] split = s.split(Array.ENUM_DELIMITER);
			x = Float.parseFloat(split[0]);
			y = Float.parseFloat(split[1]);
			width = Float.parseFloat(split[2]);
			height = Float.parseFloat(split[3]);

		} catch (final Exception e) {

			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}
	
	public static class RectangleProperty implements Property<Rectangle> {

		private final Rectangle value = new Rectangle();
		private final Information i;
		
		public RectangleProperty(String id, String tag, Flag f, Rectangle value) {
			
			this.value.applyString(value.asString());
			i = new Information(id, tag, f, value.asString());
		}
		@Override
		public Information info() {
			
			i.content = value.asString();
			return i;
		}

		@Override
		public void setFlag(Flag flag) {

			this.i.flag = flag;
		}

		@Override
		public Flag flag() {

			return i.flag;
		}

		@Override
		public void applySyncInfo(Information i) {

			if (this.i.id.equals(i.id)) {
				
				value.applyString(i.content);
				this.i.content = i.content;
				this.i.tag = i.tag;
			}
		}

		@Override
		public Rectangle get() {

			return value;
		}

		@Override
		public void set(Rectangle value) {

			this.value.applyString(value.asString());
		}
		@Override
		public Information infoFlagOnly() {

			return info();
		}
	}
}
