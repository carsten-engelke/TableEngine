package org.engine.property;

public class BooleanProperty implements Property<Boolean> {

	private boolean value;
	private Information i;
	private boolean flagged = false;

	public BooleanProperty(String id, String tag, boolean value) {

		this.value = value;
		i = new Information(id, tag, String.valueOf(value));
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
	public boolean isFlagged() {

		return flagged;
	}

	@Override
	public void setFlagged(boolean flagged) {

		this.flagged = flagged;
	}
}
