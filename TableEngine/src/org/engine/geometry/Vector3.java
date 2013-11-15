package org.engine.geometry;

import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

import com.badlogic.gdx.math.Matrix4;

public class Vector3 extends com.badlogic.gdx.math.Vector3 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Vector2 v2D = new Vector2();

	public Vector3() {
		super();
	}

	public Vector3(final float x, final float y, final float z) {
		super(x, y, z);
	}

	@Override
	public Vector3 mul(final Matrix4 matrix) {
		super.mul(matrix);
		return this;
	}
	
	public Vector2 toVector2D() {
		
		v2D.set(x, y);
		return v2D;
	}

	@Override
	public String toString() {

		return Array.ENUM_START + x + Array.ENUM_DELIMITER
				+ y + Array.ENUM_DELIMITER + z
				+ Array.ENUM_END;
	}
	
	public void fromString(String s) {
		
		try {
			s = s.substring(Array.ENUM_START.length(), s.indexOf(Array.ENUM_END));
			final float[] v = new float[3];
			final String[] a = s.split(Array.ENUM_DELIMITER);
			for (int i = 0; i < 3; i++) {

				v[i] = Float.parseFloat(a[i]);
			}

			x = v[0];
			y = v[1];
			z = v[2];
		} catch (final Exception e) {

			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}
	
	public static class Vector3Property implements Property<Vector3> {

		private Vector3 value;
		private Information i;
		private boolean flagged;

		public Vector3Property(String id, String tag, Vector3 value) {
			
			this.value = value;
			this.i = new Information(id, tag, value.toString());
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
		public Vector3 get() {

			return value;
		}

		@Override
		public void set(Vector3 value) {
			
			this.value.fromString(value.toString());
			i.content = value.toString();
		}
		@Override
		public void setFlagged(boolean flagged) {

			this.flagged = flagged;
		}
		@Override
		public boolean isFlagged() {

			return flagged;
		}
		
	}
}
