package org.engine.property;

public class InformationStringException extends Exception {

	private static final long serialVersionUID = 1L;
	private String input;

	public InformationStringException(String input) {

		this.input = input;
	}

	@Override
	public String getMessage() {

		return "String does not follow Information design (<tag>id<:tag>content</tag>):"
				+ input;
	}
}
