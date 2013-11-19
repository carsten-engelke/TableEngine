package org.engine.property;

public class LongProperty implements Property<Long> {

	private long value;
	private Information i;

	public LongProperty(String id, String tag, Flag f, long value) {

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
			value = Long.parseLong(i.content);
		}
	}

	public Long get() {

		return value;
	}

	public void set(Long value) {

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