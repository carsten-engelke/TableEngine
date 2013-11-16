package org.engine.object;

import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.property.Information;
import org.engine.property.InformationArrayStringException;
import org.engine.property.Property;
import org.engine.utils.Array;

public class BasicObject extends BasicInteractable implements Object, Synchronizable{

	/**
	 * 1 : Basic Drag n Drop
	 * 2 : Synchronization
	 * 3 : Affine Transform added
	 */
	private static final long serialVersionUID = 3L;

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

		Array<Property<?>> ap = new Array<Property<?>>(new Property<?>[] {
				metrics, z, shiftGrid, resizeGrid, rotateGrid, angle, scale });
		return ap;

	}

	@Override
	public Array<Property<?>> getPropertiesFlagged() {

		Property<?>[] props = new Property<?>[] { metrics, z, shiftGrid,
				resizeGrid, rotateGrid, angle, scale };
		Array<Property<?>> retProps = new Array<Property<?>>(props.length);
		for (Property<?> p : props) {
			if (p.flag() != Property.Flag.NONE) {
				retProps.add(p);
			}
		}
		if (retProps.getSize() > 0) {
			return retProps;
		}
		return null;
	}

	@Override
	public Flag flag() {

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

		i.flag = f;
	}

}
