package org.engine.object;

import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.geometry.Rectangle;
import org.engine.property.Information;
import org.engine.property.InformationArrayStringException;
import org.engine.property.Property;
import org.engine.utils.Array;

public class BasicObject extends BasicInteractable implements Object,
		Synchronizable {

	/**
	 * 1 : Basic Drag n Drop 2 : Synchronization 3 : Affine Transform added
	 */
	private static final long serialVersionUID = 3L;

	private Property<?>[] rigidProperties = new Property<?>[] { metrics, z,
			shiftGrid, resizeGrid, angle, rotateGrid, scale, scaleGrid };
	protected Array<Property<?>> propertyArray = new Array<Property<?>>();

	public BasicObject() {

		super();
	}

	public BasicObject(Rectangle rectangle, int shiftGrid, int angle,
			int rotateGrid) {
		super(rectangle, shiftGrid, angle, rotateGrid);
	}

	@Override
	public void setPropertiesFromInformation(final Array<Information> a) {

		for (Information i : a) {
			metrics.applySyncInfo(i);
			shiftGrid.applySyncInfo(i);
			resizeGrid.applySyncInfo(i);
			rotateGrid.applySyncInfo(i);
			angle.applySyncInfo(i);
			scale.applySyncInfo(i);
		}
	}

	public Array<Property<?>> getProperties() {

		propertyArray.clear();
		propertyArray.addAll(rigidProperties);
		return propertyArray;

	}

	@Override
	public Array<Property<?>> getPropertiesFlagged() {

		propertyArray.clear();
		for (Property<?> p : rigidProperties) {
			if (p.flag() != Property.Flag.NONE) {
				propertyArray.add(p);
			}
		}
		if (propertyArray.getSize() > 0) {
			return propertyArray;
		}
		return null;
	}

	@Override
	public Flag flag() {

		if (i.flag == Flag.NONE) {
			for (Property<?> p : getProperties()) {
				if (p.flag() != Flag.NONE) {
					i.flag = Flag.UPDATE;
				}
			}
		}
		return i.flag;
	}

	@Override
	public Information info() {

		i.content = Information.PropertiesToString(getProperties());
		return i;
	}

	@Override
	public void applySyncInfo(Information i) {

		if (i.id.equals(this.i.id)) {

			try {
				setPropertiesFromInformation(Information
						.StringToInformations(i.content));
				this.i.tag = i.tag;
				this.i.content = i.content;
			} catch (InformationArrayStringException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Synchronizable get() {

		return this;
	}

	@Override
	public void set(Synchronizable value) {

		setPropertiesFromInformation(Information.PropertiesToInformations(value
				.getProperties()));
	}

	@Override
	public void initialize(Information i, Layer l) {

		this.i = i;
		applySyncInfo(i);
		this.l = l;
	}

	@Override
	public void setFlag(Flag f) {

		for (Property<?> p : getProperties()) {
			p.setFlag(f);
		}
		i.flag = f;
	}

	@Override
	protected void stopResizing() {

		super.stopResizing();
		t.addAnimation(i.id + ":resize(" + resizeStartSize.x + ":"
				+ resizeStartSize.y + ":" + width + ":" + height + ":"
				+ dragStartPoint.x + ":" + dragStartPoint.y + ":" + x + ":" + y
				+ ")");
		metrics.setFlag(Flag.UPDATE);
	}

	@Override
	protected void stopScaling() {

		super.stopScaling();
		t.addAnimation(i.id + ":scale(" + startScale + ":" + scale.get() + ")");
		scale.setFlag(Flag.UPDATE);
	}

	@Override
	protected void stopRotating() {

		super.stopRotating();
		t.addAnimation(i.id + ":rotate(" + rotateStartAngle + ":" + angle + ")");
		angle.setFlag(Flag.UPDATE);
	}

	@Override
	protected void stopShifting() {

		super.stopShifting();
		t.addAnimation(i.id + ":shift(" + dragStartPoint.x + ":"
				+ dragStartPoint.y + ":" + getX() + ":" + getY() + ")");
		metrics.setFlag(Flag.UPDATE);
		z.setFlag(Flag.UPDATE);
	}

	@Override
	public Information infoFlagOnly() {
		
		propertyArray = getPropertiesFlagged();
		if (propertyArray != null && propertyArray.getSize() > 0) {
			i.content = Information.PropertiesToString(propertyArray);
			return i;
		}
		return null;
	}
}
