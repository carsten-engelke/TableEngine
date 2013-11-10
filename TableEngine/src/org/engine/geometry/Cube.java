/*
 * 
 */
package org.engine.geometry;

import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;
import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

public class Cube {

	public static String getDefaultBoundsString() {

		return new Cube().getBoundsString();
	}

	public float depth = 100;

	public float height = 100;

	public float width = 100;

	public float x = 0;

	public float y = 0;

	public float z = 10000;

	public Cube() {

	}

	public Cube(final Cube c) {

		setX(c.getX());
		setY(c.getY());
		setZ(c.getZ());
		setWidth(c.getWidth());
		setHeight(c.getHeight());
		setDepth(c.getDepth());
	}

	/**
	 * Instantiates a new cube.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 */
	public Cube(final float x, final float y, final float z, final float width,
			final float height, final float depth) {

		setX(x);
		setY(y);
		setZ(z);
		setWidth(width);
		setHeight(height);
		setDepth(depth);
	}

	/**
	 * Instantiates a new cube.
	 * 
	 * @param s
	 *            the s
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public Cube(String s) throws IllegalArgumentException {

		applyBoundsString(s);
	}

	/**
	 * Instantiates a new cube.
	 * 
	 * @param p
	 *            the p
	 * @param d
	 *            the d
	 */
	public Cube(final Vector3 position, final Vector3 dimension) {

		setX(position.x);
		setY(position.y);
		setZ(position.z);
		setWidth(dimension.x);
		setHeight(dimension.y);
		setDepth(dimension.z);
	}

	public Cube getBounds() {

		return this;
	}

	/**
	 * Gets the bounds string.
	 * 
	 * @return the bounds string
	 */
	public String getBoundsString() {

		return Array.ENUM_START + getX()
			+ Array.ENUM_DELIMITER + getY()
				+ Array.ENUM_DELIMITER + getZ()
				+ Array.ENUM_DELIMITER + getWidth()
				+ Array.ENUM_DELIMITER + getHeight()
				+ Array.ENUM_DELIMITER + getDepth()
				+ Array.ENUM_END;
	}

	public void applyBoundsString(String s) {
		
		try {
			s = s.substring(Array.ENUM_START.length(), s.indexOf(Array.ENUM_END));
			final float[] bounds = new float[6];
			final String[] a = s.split(Array.ENUM_DELIMITER);
			for (int i = 0; i < 6; i++) {

				bounds[i] = Float.parseFloat(a[i]);
			}

			setX(bounds[0]);
			setY(bounds[1]);
			setZ(bounds[2]);
			setWidth(bounds[3]);
			setHeight(bounds[4]);
			setDepth(bounds[5]);

		} catch (final Exception e) {

			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Gets the depth.
	 * 
	 * @return the depth
	 */
	public float getDepth() {

		return depth;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public float getHeight() {

		return height;
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public Vector3 getLocation() {

		return new Vector3(getX(), getY(), getZ());

	}

	/**
	 * Gets the location2 d.
	 * 
	 * @return the location2 d
	 */
	public Vector2 getLocation2D() {

		return new Vector2(getX(), getY());

	}

	/**
	 * Gets the rectangle2 d.
	 * 
	 * @return the rectangle2 d
	 */
	public Rectangle getRectangle2D() {

		return new Rectangle(getX(), getY(), getWidth(), getHeight());

	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public Vector3 getSize() {

		return new Vector3(getWidth(), getHeight(), getDepth());
	}

	/**
	 * Gets the size2 d.
	 * 
	 * @return the size2 d
	 */
	public Vector2 getSize2D() {

		return new Vector2(getWidth(), getHeight());
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public float getWidth() {

		return width;
	}

	/**
	 * Gets the x.
	 * 
	 * @return the x
	 */
	public float getX() {

		return x;
	}

	/**
	 * Gets the y.
	 * 
	 * @return the y
	 */
	public float getY() {

		return y;
	}

	/**
	 * Gets the z.
	 * 
	 * @return the z
	 */
	public float getZ() {

		return z;
	}

	public void setBounds(final Cube c) {

		setX(c.getX());
		setY(c.getY());
		setZ(c.getZ());
		setWidth(c.getWidth());
		setHeight(c.getHeight());
		setDepth(c.getDepth());
	}

	public void setBounds(final float x, final float y, final float z,
			final float w, final float h, final float d) {

		setX(x);
		setY(y);
		setZ(z);
		setWidth(w);
		setHeight(h);
		setDepth(d);
	}

	/**
	 * Sets the depth.
	 * 
	 * @param d
	 *            the new depth
	 */
	public void setDepth(final float d) {

		depth = d;
	}

	/**
	 * Sets the height.
	 * 
	 * @param h
	 *            the new height
	 */
	public void setHeight(final float h) {

		height = h;
	}

	/**
	 * Sets the location.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setLocation(final float x, final float y, final float z) {

		setX(x);
		setY(y);
		setZ(z);

	}

	/**
	 * Sets the location.
	 * 
	 * @param p
	 *            the new location
	 */
	public void setLocation(final Vector3 p) {

		setX(p.x);
		setY(p.y);
		setZ(p.z);

	}

	/**
	 * Sets the size.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 */
	public void setSize(final float width, final float height, final float depth) {

		setWidth(width);
		setHeight(height);
		setDepth(depth);

	}

	/**
	 * Sets the size.
	 * 
	 * @param d
	 *            the new size
	 */
	public void setSize(final Vector3 d) {

		setWidth(d.x);
		setHeight(d.y);
		setDepth(d.z);

	}

	/**
	 * Sets the width.
	 * 
	 * @param x
	 *            the new width
	 */
	public void setWidth(final float w) {

		width = w;
	}

	/**
	 * Sets the x.
	 * 
	 * @param f
	 *            the new x
	 */
	public void setX(final float x) {

		this.x = x;
	}

	/**
	 * Sets the y.
	 * 
	 * @param f
	 *            the new y
	 */
	public void setY(final float y) {

		this.y = y;
	}

	/**
	 * Sets the z.
	 * 
	 * @param z
	 *            the new z
	 */
	public void setZ(final float z) {

		this.z = z;
	}

	@Override
	public String toString() {

		return getBoundsString();
	}
	
	public class CubeProperty implements Property<Cube>
	{

		private Cube value;
		private Information i;
		private boolean flagged;
		
		public CubeProperty(String id, String tag, Cube value) {
			
			this.value = value;
			// this assignment is fixed. Every call on this property will only retrieve the hereby assigned object!
			i = new Information(id, tag, value.getBoundsString());
		}
		
		public Information info()
		{
			i.content = value.getBoundsString();
			return i;
		}

		public void applySyncInfo(Information i)
		{
			if (i.id.equals(this.i.id)) {
				
				this.i.tag = i.tag;
				this.i.content = i.content;
				value.applyBoundsString(i.content);
			}
		}

		public Cube get()
		{
		
			return value;
		}

		public void set(Cube value)
		{
			this.value.setBounds(value);
			i.content = value.getBoundsString();
		}

		@Override
		public void setFlagged(boolean flagged) {

			this.flagged = flagged;
		}

		@Override
		public boolean isFlagged() {
			// TODO Auto-generated method stub
			return flagged;
		}
	}
}
