package org.engine.editor;

public class Property {

	public String content = "";
	public String name = "";
	public String type = "";

	Property() {

	}

	Property(final String name, final String type, final String content) {

		this.name = name;
		this.type = type;
		this.content = content;
	}

	Property(final String[] arrayProps) {

		name = arrayProps[0];
		type = arrayProps[1];
		content = arrayProps[2];
	}

	@Override
	public String toString() {

		return name + ";" + type + ";" + content;
	}
}
