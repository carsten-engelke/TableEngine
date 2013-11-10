package org.engine.menu;

import org.engine.Interactable;

public interface MenuItem extends Interactable {

    public void setVisible(boolean b);
	public boolean isVisible();
	public int getPreferredPosition();
	public String getLabel();
}
