package org.engine.gui;

import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

/**
 * The Class NullGUI. Just for testing purposes. does nothing.
 */
public class NullGUI implements GUI {

	Layer l;

	@Override
	public Layer getParentLayer() {

		return l;
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {
		return false;
	}

	@Override
	public void output(final Graphics g) {

	}

	@Override
	public void initialize(final Layer l) {

		this.l = l;
	}

}
