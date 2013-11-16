package org.engine.property;

public class FloatProperty implements Property<Float> {

	private float value;
	private Information i;

	public FloatProperty(String id, String tag, Flag f, float value) {

		this.value = value;
		this.i = new Information(id, tag, f, String.valueOf(value));
	}

	public Information info() {

		return i;
	}

	public void applySyncInfo(Information i) {
		if (i.id.equals(this.i.id)) {

			this.i.content = i.content;
			this.i.tag = i.tag;
			value = Float.parseFloat(i.content);
		}
	}

	public Float get() {

		return value;
	}

	public void set(Float value) {
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
}