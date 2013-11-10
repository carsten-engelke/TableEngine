/*
 * 
 */
package org.engine.gui;

import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.property.Information;
import org.engine.property.Property;

public interface GUI {

	public Layer getParentLayer();

	public boolean input(InputEvent e, boolean wasCatchedAbove);

	public void output(Graphics g);

	public void initialize(Layer l);

	public static class GUIProperty implements Property<GUI> {

		private GUI value;
		private Information i;
		private boolean flagged = false;

		public GUIProperty(String id, String tag, GUI value) {

			this.value = value;
			i = new Information(id, tag, getInfoString());
		}
		@Override
		public Information info() {

			i.content = getInfoString();
			return i;
		}

		@Override
		public void applySyncInfo(Information i) {

			if (i.id.equals(this.i.id)) {
				if (i.content.equals("PLAIN") && !PlainGUI.class.isAssignableFrom(value.getClass())) {
					Layer l = value.getParentLayer();
					value = new PlainGUI();
					value.initialize(l);
				}
				if (i.content.equals("TRANS") && !TransformGUI.class.isAssignableFrom(value.getClass())) {
					Layer l = value.getParentLayer();
					value = new TransformGUI();
					value.initialize(l);
				}
			}
		}

		@Override
		public GUI get() {

			return value;
		}

		@Override
		public void set(GUI value) {

			this.value = value;
			i.content = getInfoString();
		}
		
		private String getInfoString() {
			
			if (PlainGUI.class.isAssignableFrom(value.getClass())) {
				return "PLAIN";
			}
			if (TransformGUI.class.isAssignableFrom(value.getClass())) {
				return "TRANS";
			}
			return null;
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
}
