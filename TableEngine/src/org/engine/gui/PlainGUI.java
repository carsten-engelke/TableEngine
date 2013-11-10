/*
 * 
 */
package org.engine.gui;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.utils.SortableInteractableArray;

/**
 * The Class PlainGUI. A simple class to handle the i/o for objects.
 */
public class PlainGUI implements GUI {

	private Layer l;

	@Override
	public Layer getParentLayer() {

		return l;
	}

	@Override
	public boolean input(final InputEvent e, boolean wasCatchedAbove) {

		final SortableInteractableArray depthList = new SortableInteractableArray();
		depthList.addAll(l.getAllInteractables());
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
		for (final Interactable i : depthList) {

			if (i.input(e, wasCatchedAbove)) {

				wasCatchedAbove = true;
			}

		}

		return wasCatchedAbove;
	}

	@Override
	public void output(final Graphics g) {
		final SortableInteractableArray depthList = new SortableInteractableArray();
		depthList.addAll(l.getAllInteractables());
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_ABOVE);
		for (final Interactable i : depthList) {
			g.reset();
			i.output(g);
		}
	}

	@Override
	public void initialize(final Layer l) {

		this.l = l;

	}

}
