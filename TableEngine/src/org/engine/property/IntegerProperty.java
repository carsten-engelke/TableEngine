package org.engine.property;

public class IntegerProperty implements Property<Integer> {

	private int value = -1;
	private Information i;

	public IntegerProperty(String id, String tag, Flag f, int value) {

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
			value = Integer.parseInt(i.content);
		}
	}

	public Integer get() {

		return value;
	}

	public void set(Integer value) {
		this.value = value;
		i.content = String.valueOf(value);
	}

	@Override
	public Flag flag() {

		return i.flag;
	}

	@Override
	public void setFlag(Flag f) {

		this.i.flag = f;
	}

	@Override
	public Information infoFlagOnly() {

		return info();
	}
}
