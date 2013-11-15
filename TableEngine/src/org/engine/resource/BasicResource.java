package org.engine.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import org.engine.TableEngine;
import org.engine.gui.TransformGUI;
import org.engine.language.BasicDefaultLanguage;
import org.engine.language.Language;
import org.engine.utils.ArrayMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class BasicResource implements Resource {

	public static final String DESCRIPTION_ID = "basic";

	private static TextureAtlas ta;

	public static void main(String[] args) {

		try {
			System.out.println("PACKING BASIC RESOURCE\n");
			doPack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pack Resources into TextureAtlases and data into right directories. Works
	 * only (!) on Desktop!
	 */
	public static void doPack() throws IOException {

		System.out.println(System.getProperty("user.dir"));
		// REMOVE OLD FILES
		System.out.print("REMOVE OLD FILES...");
		File f = new File(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
						+ BasicResource.DESCRIPTION_ID);
		for (final File removeMe : f.listFiles()) {
			removeMe.delete();
		}
		System.out.println("done");
		new File("C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
				+ BasicResource.DESCRIPTION_ID + "/").mkdirs();

		// PACK IMAGES
		final Settings s = new Settings();
		// s.maxWidth = 1024;
		s.maxHeight = 1024;
		TexturePacker2.process(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/images",
				"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
						+ BasicResource.DESCRIPTION_ID, "pics");

		// PACK SKIN
		// Standard ist Holo-Dark-Mdpi !!! -> Packen und als ui.json
		// abspeichern
		s.maxWidth = 512;
		final String col = "dark";
		final String size = "mdpi";
		packSkin(s, col, size);

		// PACK SOUNDS
		f = new File("C:/Users/Carsten/EngineWorkspace/TableEngine/sounds");
		for (final File file : f.listFiles()) {

			System.out.println(BasicResource.DESCRIPTION_ID + "/"
					+ file.getName());
			Files.copy(
					file.toPath(),
					new File(
							"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
									+ BasicResource.DESCRIPTION_ID + "/"
									+ file.getName()).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}

		// PACK DATA

		f = new File("C:/Users/Carsten/EngineWorkspace/TableEngine/data");
		for (final File file : f.listFiles()) {

			System.out.println(BasicResource.DESCRIPTION_ID + "/"
					+ file.getName());
			Files.copy(
					file.toPath(),
					new File(
							"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
									+ BasicResource.DESCRIPTION_ID + "/"
									+ file.getName()).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}
		System.out.println("   DONE -> EXIT");
	}

	public static void packAllSkins() throws IOException {

		// PACK SKINS
		final Settings s = new Settings();
		s.maxHeight = 1024;
		s.maxWidth = 512;

		String[] sizes = new String[] { "ldpi", "mdpi", "hdpi", "xhdpi" };
		String[] colors = new String[] { "dark", "light" };

		for (String col : colors) {
			for (String size : sizes) {

				packSkin(s, col, size);
			}
		}
	}

	private static void packSkin(Settings s, String col, String size) throws IOException {
		
		TexturePacker2.process(s,
				"C:/Users/Carsten/EngineWorkspace/TableEngine/ui/" + col
						+ "/drawable-" + size,
				"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
						+ BasicResource.DESCRIPTION_ID, "ui-" + col + "-"
						+ size);
		// Copy Font Files

		Files.copy(new File(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/ui/fonts/Roboto-"
						+ size + ".fnt").toPath(), new File(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
						+ BasicResource.DESCRIPTION_ID + "/Roboto-" + size
						+ ".fnt").toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(new File(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/ui/fonts/Roboto-Thin-"
						+ size + ".fnt").toPath(), new File(
				"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
						+ BasicResource.DESCRIPTION_ID + "/Roboto-Thin-" + size
						+ ".fnt").toPath(), StandardCopyOption.REPLACE_EXISTING);
		try {
			final BufferedReader br = new BufferedReader(new FileReader(
					"C:/Users/Carsten/EngineWorkspace/TableEngine/ui/ui.json"));
			final FileWriter fw = new FileWriter(
					"C:/Users/Carsten/EngineWorkspace/TableEngine/assets/"
							+ BasicResource.DESCRIPTION_ID + "/ui-" + col + "-"
							+ size + ".json");
			String line = br.readLine();
			while (line != null) {

				line = line.replace("<col>", col);
				line = line.replace("<size>", size);
				fw.write(line + "\n");
				fw.flush();
				line = br.readLine();
			}
			fw.close();
			br.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	public static void unpackTextureAtlas(final FileHandle atlas) {

		BasicResource.ta = new TextureAtlas(atlas);
		final Pixmap p = new Pixmap(Gdx.files.internal(atlas.parent() + "/"
				+ atlas.nameWithoutExtension() + ".png"));
		final String aim = "EngineWorkspace/TableEngine-android/assets/test/input";
		final FileHandle faim = Gdx.files.external(aim);
		faim.mkdirs();
		for (final AtlasRegion ar : BasicResource.ta.getRegions()) {

			if (ar.splits != null) {

				// Create .9.png
				final int a = ar.splits[0];
				final int b = ar.splits[1];
				final int c = ar.splits[2];
				final int d = ar.splits[3];
				final Pixmap patch = new Pixmap(ar.getRegionWidth() + 2,
						ar.getRegionHeight() + 2, Pixmap.Format.RGBA8888);
				patch.drawPixmap(p, ar.getRegionX(), ar.getRegionY(),
						ar.getRegionWidth(), ar.getRegionHeight(), 1, 1,
						ar.getRegionWidth(), ar.getRegionHeight());
				patch.setColor(Color.BLACK);
				patch.drawLine(1 + a, 0, ar.getRegionWidth() - b, 0);
				patch.drawLine(0, 1 + c, 0, ar.getRegionHeight() - d);
				patch.drawLine(1 + a, 1 + ar.getRegionHeight(),
						ar.getRegionWidth() - b, 1 + ar.getRegionHeight());
				patch.drawLine(1 + ar.getRegionWidth(), 1 + c,
						1 + ar.getRegionWidth(), ar.getRegionHeight() - d);
				final FileHandle f = Gdx.files.external(aim + "/" + ar.name
						+ ".9.png");
				PixmapIO.writePNG(f, patch);
			} else {
				final Pixmap patch = new Pixmap(ar.getRegionWidth(),
						ar.getRegionHeight(), Pixmap.Format.RGBA8888);
				patch.drawPixmap(p, ar.getRegionX(), ar.getRegionY(),
						ar.getRegionWidth(), ar.getRegionHeight(), 0, 0,
						ar.getRegionWidth(), ar.getRegionHeight());
				final FileHandle f = Gdx.files.external(aim + "/" + ar.name
						+ ".png");
				PixmapIO.writePNG(f, patch);
			}
		}
	}

	ArrayMap<String, String> dataDB = new ArrayMap<String, String>();

	ArrayMap<String, TextureRegion> imageDB = new ArrayMap<String, TextureRegion>();

	ArrayMap<String, Skin> skinDB = new ArrayMap<String, Skin>();

	ArrayMap<Locale, Language> languageDB = new ArrayMap<Locale, Language>();

	ArrayMap<String, Sound> soundDB = new ArrayMap<String, Sound>();

	String[] soundNames = new String[] { "drop.wav", "shuffle.wav", "draw.wav",
			"die.wav", "coin.wav" };

	public BasicResource() {

		loadDB();
	}

	@Override
	public void dispose() {

		BasicResource.ta.dispose();

		for (final Sound s : soundDB.values()) {

			s.dispose();
		}
	}

	@Override
	public String getData(final String key) {

		return dataDB.getValue(key);
	}

	@Override
	public String getID() {

		return BasicResource.DESCRIPTION_ID;
	}

	@Override
	public TextureRegion getImage(final String key) {

		return imageDB.getValue(key);
	}

	@Override
	public Language getLanguage(final Locale locale) {

		if (languageDB.containsKey(locale)) {
			return languageDB.getValue(locale);
		}
		return null;
	}

	@Override
	public Preferences getDefaultPreferences() {

		final Preferences p = Gdx.app
				.getPreferences(BasicResource.DESCRIPTION_ID);
		return loadDefaultPreferences(p);
	}

	@Override
	public Skin getSkin(final String name) {
		return skinDB.getValue(name);
	}

	@Override
	public Sound getSound(final String name) {

		return soundDB.getValue(name);
	}

	@Override
	public void initialize(final TableEngine tableEngine) {
	}

	private void loadDB() {

		// READ IMAGES
		final FileHandle picAtlas = Gdx.files
				.internal(BasicResource.DESCRIPTION_ID + "/pics.atlas");
		if (picAtlas.exists()) {
			final TextureAtlas ta = new TextureAtlas(picAtlas);
			for (final AtlasRegion ar : ta.getRegions()) {
				imageDB.put(ar.name, ar);
			}
		}

		// READ SOUNDS
		for (final String sound : soundNames) {
			soundDB.put(sound.substring(0, sound.indexOf(".")), Gdx.audio
					.newSound(Gdx.files.internal(BasicResource.DESCRIPTION_ID
							+ "/" + sound)));

		}

		// READ SKIN
		final Skin s = new Skin(Gdx.files.internal(BasicResource.DESCRIPTION_ID
				+ "/ui-dark-mdpi.json"));
		skinDB.put(BasicResource.DESCRIPTION_ID, s);

		// LOAD LANGUAGE
		final Language l = new BasicDefaultLanguage();
		languageDB.put(Locale.ENGLISH, l);
	}

	private Preferences loadDefaultPreferences(final Preferences p) {

		if (p.get().size() == 0) {
			// THIS IS THE PLACE TO STORE BASIC PREFERENCES
			p.putInteger("gameID", 234);
			p.putString("key", "0");
			p.putString("bgColor", "0D686B");
			p.putString("game", "TableEngine");
			p.putString("playerName", "Player");
			p.putString("playerAvatar", "0");
			p.putString("playerID", "player.name@player.com");
			new TransformGUI.TransformView().toPreferences(p);
			p.putString("language", Locale.getDefault().toLanguageTag());
			p.putBoolean("showCompass", true);
			p.putBoolean("showTimeLine", true);
			p.putBoolean("showChat", true);
			p.putBoolean("debug", false);
			try {
				BufferedReader br = new BufferedReader(new FileReader(Gdx.files
						.internal("basic/server").file()));
				p.putString("url", br.readLine());
				br.close();
			} catch (Exception e) {
				p.putString("url", "http://localhost");
			}
		}
		return p;
	}

	@Override
	public void resetDatabases() {

		imageDB.clear();
		dataDB.clear();
		loadDB();

	}
}
