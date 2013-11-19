package org.engine.property;

public class BooleanProperty implements Property<Boolean> {

	private boolean value;
	private Information i;

	public BooleanProperty(String id, String tag, Flag f, boolean value) {

		this.value = value;
		i = new Information(id, tag, f, String.valueOf(value));
	}

	public Information info() {

		return i;
	}

	public void applySyncInfo(Information i) {
		if (i.id.equals(this.i.id)) {

			this.i.content = i.content;
			this.i.tag = i.tag;
			value = Boolean.parseBoolean(i.content);
		}
	}

	public Boolean get() {

		return value;
	}

	public void set(Boolean value) {
		this.value = value;
		i.content = String.valueOf(value);
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
