/*
 * 
 */
package org.engine.language;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.engine.resource.BasicResource;

import com.badlogic.gdx.files.FileHandle;

/**
 * The Class Language.
 */
public class Language extends Properties {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	public String DESCRIPTION_ID;

	/** The locale of the language. */
	public Locale locale;

	public Language() {
		locale = Locale.getDefault();
	}

	public Language(final FileHandle file) {

		try {
			load(file.read());
			locale = new Locale(getProperty("localeLanguage"),
					getProperty("localeCountry"));
			DESCRIPTION_ID = getProperty("descriptionID");
		} catch (final IOException e) {
			e.printStackTrace();
			if (locale == null) {
				locale = Locale.getDefault();
			}
			if (DESCRIPTION_ID == null) {
				DESCRIPTION_ID = BasicResource.DESCRIPTION_ID;
			}
		}
	}

	public Language(final Locale locale) {
		this.locale = locale;
	}

	public void setDefault(final Language l) {

		defaults = l;
	}

	@Override
	public synchronized String toString() {
		return locale.getDisplayLanguage() + " " + super.toString();
	}
}
