package org.engine.network;

public enum Command {

	Receive, Send;

	@Override
	public String toString() {

		if (this == Send) {
			return "SEND";
		}

		if (this == Receive) {
			return "RECEIVE";
		}
		return null;
	};
}
