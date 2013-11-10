package org.engine;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface Skinnable {
	
	public void style(Skin skin);
	public void style(Skin skin, String style);
	public void style(Style style);
	public void adaptToScreenSize(int width, int height);
	
	// The class Style is just a containe for Drawables, see Menu.java for use.
	public class Style {
		
	}
}
