package org.engine.property;

public class InformationArrayStringException extends Exception {

	private static final long serialVersionUID = 1L;
	private String input;

	public InformationArrayStringException(String input) {

		this.input = input;
	}

	@Override
	public String getMessage() {

		return "String does not follow Information design (<tag>id<:tag>content<tag>id2<:tag>content2</tag>):"
				+ input;
	}
}