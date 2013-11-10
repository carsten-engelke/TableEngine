package org.engine.magnifier;

import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.BasicObject;

public class NullMagnifier extends BasicObject implements Magnifier {

	private String focus = "";
	private float magn = 0.0F;

	@Override
	public Object getFocussedObject() {

		return focus;
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {
		return wasCatchedAbove;
	}

	@Override
	public float isMagnificating() {
		return magn;
	}

	@Override
	public void output(final Graphics g) {

	}

	@Override
	public void setFocussedObject(final String id) {

		focus = id;
	}

	@Override
	public void setMagnificating(final float multiplier) {
		magn = multiplier;
	}
}
