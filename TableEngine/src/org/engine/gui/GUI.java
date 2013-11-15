package org.engine.gui;

import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

public interface GUI {

	public Layer getParentLayer();

	public boolean input(InputEvent e, boolean wasCatchedAbove);

	public void output(Graphics g);

	public void initialize(Layer l);
}
