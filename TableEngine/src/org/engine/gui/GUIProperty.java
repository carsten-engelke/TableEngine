package org.engine.gui;

import org.engine.Layer;
import org.engine.property.Information;
import org.engine.property.Property;

public class GUIProperty implements Property<GUI> {

	private GUI value;
	private Information i;

	public GUIProperty(String id, String tag, Flag f, GUI value) {

		this.value = value;
		i = new Information(id, tag, f, getInfoString());
	}
	@Override
	public Information info() {

		i.content = getInfoString();
		return i;
	}

	@Override
	public void applySyncInfo(Information i) {

		if (i.id.equals(this.i.id)) {
			if (i.content.equals("PLAIN") && !PlainGUI.class.isAssignableFrom(value.getClass())) {
				Layer l = value.getParentLayer();
				value = new PlainGUI();
				value.initialize(l);
			}
			if (i.content.equals("TRANS") && !TransformGUI.class.isAssignableFrom(value.getClass())) {
				Layer l = value.getParentLayer();
				value = new TransformGUI();
				value.initialize(l);
			}
		}
	}

	@Override
	public GUI get() {

		return value;
	}

	@Override
	public void set(GUI value) {

		this.value = value;
		i.content = getInfoString();
	}
	
	private String getInfoString() {
		
		if (PlainGUI.class.isAssignableFrom(value.getClass())) {
			return "PLAIN";
		}
		if (TransformGUI.class.isAssignableFrom(value.getClass())) {
			return "TRANS";
		}
		return null;
	}
	@Override
	public Flag flag() {

		return i.flag;
	}
	@Override
	public void setFlag(Flag f) {

		i.flag = f;
	}
	@Override
	public Information infoFlagOnly() {

		return info();
	}
}
