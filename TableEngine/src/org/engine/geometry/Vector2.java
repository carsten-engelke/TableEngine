package org.engine.geometry;

import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

public class Vector2 extends com.badlogic.gdx.math.Vector2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Vector3 v3D = new Vector3();
	
	public Vector2() {
		super();
	}

	public Vector2(final float x, final float y) {
		super(x, y);
	}

	@Override
	public String toString() {

		return Array.ENUM_START + x + Array.ENUM_DELIMITER
				+ y + Array.ENUM_END;
	}
	
	public Vector3 toVector3() {
		
		v3D.set(x, y, 0);
		return v3D;
	}

	public void fromString(String s) {

		try {
			s = s.substring(Array.ENUM_START.length(), s.indexOf(Array.ENUM_END));
			final float[] v = new float[2];
			final String[] a = s.split(Array.ENUM_DELIMITER);
			for (int i = 0; i < 2; i++) {

				v[i] = Float.parseFloat(a[i]);
			}

			x = v[0];
			y = v[1];
		} catch (final Exception e) {

			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}
	
	public static class Vector2Property implements Property<Vector2> {

		private Vector2 value;
		private Information i;

		public Vector2Property(String id, String tag, Flag f, Vector2 value) {
			
			this.value = value;
			this.i = new Information(id, tag, f, value.toString());
		}
		@Override
		public Information info() {
			i.content = value.toString();
			return i;
		}

		@Override
		public void applySyncInfo(Information i) {

			if (i.id.equals(this.i.id)) {
				
				value.fromString(i.content);
				this.i.tag = i.tag;
				this.i.content = i.content;
			}			
		}

		@Override
		public Vector2 get() {

			return value;
		}

		@Override
		public void set(Vector2 value) {
			
			this.value.fromString(value.toString());
			i.content = value.toString();
		}
		@Override
		public void setFlag(Flag f) {

			i.flag = f;
		}
		@Override
		public Flag flag() {

			return i.flag;
		}
		@Override
		public Information infoFlagOnly() {

			return info();
		}
		
	}
}
