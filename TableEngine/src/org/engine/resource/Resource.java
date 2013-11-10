package org.engine.resource;

import java.util.Locale;

import org.engine.TableEngine;
import org.engine.language.Language;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface Resource {

	public void dispose();

	public String getData(String name);

	public String getID();

	public TextureRegion getImage(String name);

	public Sound getSound(String name);
	
	public Skin getSkin(String name);
	
	public Preferences getDefaultPreferences();
	
	public Language getLanguage(Locale locale);
	
	public void initialize(TableEngine parent);

	public void resetDatabases();
}
