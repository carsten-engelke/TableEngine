package org.engine.gui.input;

import org.engine.Synchronizable;

public class ActionEvent extends InputEvent {

	public static final String COMMAND_CLICK = "CLICK";
	public String actionCommand;

	public Synchronizable origin;

	public ActionEvent(final InputEvent e, final Synchronizable origin,
			final String actionCommand) {
		super(e);
		this.origin = origin;
		this.actionCommand = actionCommand;
	}
}
