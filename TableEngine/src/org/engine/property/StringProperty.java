package org.engine.property;

public class StringProperty implements Property<String> {

	private Information i;
	private boolean flagged = false;

	public StringProperty(String id, String tag, String content) {

		i = new Information(id, tag, content);
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
	public boolean isFlagged() {

		return flagged;
	}

	@Override
	public void setFlagged(boolean flagged) {

		this.flagged = flagged;
	}
}
