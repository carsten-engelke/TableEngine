package org.engine;

import org.engine.menu.Menu;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Objects that are affected from the TableEngine's Skin are informed about
 * changes in Skin or resolution via this interface.
 */
public interface Skinnable {

	/**
	 * Assigns a new skin to the Skinnable object, this should adopt the "default" style from it.
	 * 
	 * @param skin
	 *            the new skin
	 */
	public void style(Skin skin);

	/**
	 * Assigns a new style from a skin (via it's String name) to the Skinnable object.
	 * 
	 * @param skin
	 *            the new skin
	 * @param style
	 *            the new style's name
	 */
	public void style(Skin skin, String style);

	/**
	 * Assigns a new style to the Skinnable object.
	 * 
	 * @param style
	 *            the new style
	 */
	public void style(Style style);

	/**
	 * Adapt to screen size. Called when the TableEngine is resized.
	 * 
	 * @param width
	 *            the new width
	 * @param height
	 *            the new height
	 */
	public void adaptToScreenSize(int width, int height);

	/**
	 * The class Style is just a containe for {@link Drawable} objects, see {@link Menu} for use.
	 */
	public class Style {

	}
}
