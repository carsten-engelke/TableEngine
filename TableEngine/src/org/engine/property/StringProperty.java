package org.engine.property;

public class StringProperty implements Property<String> {

	private Information i;

	public StringProperty(String id, String tag, Flag f, String content) {

		i = new Information(id, tag, f, content);
	}

	public void applySyncInfo(Information i) {
		if (i.id.equals(this.i.id)) {
			this.i.tag = i.tag;
			this.i.content = i.content;
		}
	}

	public String get() {

		return i.content;
	}

	public void set(String value) {

		i.content = value;
	}

	public Information info() {

		return i;
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
