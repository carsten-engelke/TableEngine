package org.engine;

import java.util.Locale;

import org.engine.gui.PlainGUI;
import org.engine.gui.TransformGUI;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.language.BasicDefaultLanguage;
import org.engine.language.Language;
import org.engine.loader.UniverseLoader;
import org.engine.magnifier.Magnifier;
import org.engine.magnifier.NullMagnifier;
import org.engine.menu.Menu;
import org.engine.model.PlainModel;
import org.engine.network.InterruptionListener;
import org.engine.network.Network;
import org.engine.network.PlainNetwork;
import org.engine.resource.BasicResource;
import org.engine.resource.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.engine.utils.Array;
import org.engine.utils.ArrayMap;
import org.engine.utils.SortableLayerArray;

/**
 * The Class Universe. Contains layers and synchronizes them via network.
 */
public class Universe {

	public static final String LayerBackGround = "BackgroundLayer";
	public static final String LayerForeGround = "ForegroundLayer";
	public static final String LayerMagnifier = "MagnifierLayer";
	public static final String LayerMenu = "MenuLayer";
	public static final String LayerMenuPopUp = "MenuPopUpLayer";
	public static final String LayerSystem = "SystemLayer";

	/**
	 * HISTORY: Version 1: Import functionality from SwingGUI.
	 */
	protected static final long serialVersionUID = 1L;

	protected Language activeLanguage;

	public Color backgroundColor = new Color(5, 70, 30, 1);

	protected boolean busy;

    
	private Magnifier magnifier;

	public Menu menu;

	public Network network;

	public TableEngine tableEngine;

	public String startBGLayer;

	public String startLanguage;

	private System system;

	protected int view;

	/**
	 * Instantiates a new universe.
	 */
	public Universe(final String id, final Resource[] resources,
			final TableEngine parent) {

		gameID = id;
		userID = id + Math.random();
		// Set Loading Image "/resources/logo.png"
		this.tableEngine = parent;
		init(resources);
		// Stop Loading Image
		Gdx.graphics.requestRendering();
	}

	/**
	 * Adds the busy listener.
	 * 
	 * @param l
	 *            the l
	 */
	public void addBusyListener(final InterruptionListener l) {

		BusyArray.add(l);
	}

	public String addLayer(final Layer layer) {

		layerMap.put(layer.getLabel(), layer);
		layer.setParentUniverse(this);
		return layer.getLabel();
	}

	public void dispose() {

		for (final Resource r : resourceMap.values()) {

			r.dispose();
		}
	}

	public Array<Language> getAllLanguages() {

		return new Array<Language>(languageMap.values());
	}

	/**
	 * Gets the all layers.
	 * 
	 * @return the all layers
	 */
	public Array<Layer> getAllLayers() {

		return new Array<Layer>(layerMap.values());
	}

	/**
	 * Gets the all synchronized layers.
	 * 
	 * @return the all synchronized layers
	 */
	public Array<Layer> getAllSynchronizedLayers() {

		final ArrayMap<String, Layer> syncMap = new ArrayMap<String, Layer>();
		syncMap.addAll(layerMap);
		final Array<String> removeList = new Array<String>();
		for (final Layer l : syncMap.values()) {

			if (!l.isSynchronized()) {

				// Must not remove directly since classList is used in for loop
				removeList.add(l.getLabel());
			}

		}
		for (int i = 0; i < removeList.getSize(); i++) {

			syncMap.removeKey(removeList.get(i));
		}
		return new Array<Layer>(syncMap.values());
	}

	public Language getLanguage(final Locale locale) {

		for (final Language l : getAllLanguages()) {
			if (l.locale.getLanguage().equals(locale.getLanguage())) {
				return l;
			}
		}
		// System.out.println("Locale not found, will show english text:"
		// + languageMap.size);
		return languageMap.getValue(Locale.ENGLISH);
	}

	public ArrayMap<Locale, Language> getLanguageMap() {

		return languageMap;
	}

	/**
	 * Gets the layer with the described label.
	 * 
	 * @param label
	 *            the description string
	 * @return the layer
	 */
	public Layer getLayer(final String label) {

		if (layerMap.containsKey(label)) {
			return layerMap.getValue(label);

		} else {
			throw new NullPointerException(
					"Project layout incongruent - Label missing" + label);
		}
	}

	public Magnifier getMagnifier() {

		return magnifier;
	}

	public Menu getMenu() {
		return menu;
	}

	/**
	 * Gets the network.
	 * 
	 * @return the network
	 */
	public Network getNetwork() {

		return network;
	}

	public TableEngine getParentTableEngine() {
		return tableEngine;
	}

	/**
	 * Gets the user focusImage.
	 * 
	 * @return the user focusImage
	 */
	public String getPersonalPlayerID() {

		return userID;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getPersonalPlayerName() {

		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network#getViewNo()
	 */
	/**
	 * Gets the view no.
	 * 
	 * @return the view no
	 */
	public int getPersonalPlayerView() {

		return view;
	}

	public Resource getResource(final String description) {

		if (resourceMap.containsKey(description)) {
			return resourceMap.getValue(description);
		} else {
			throw new NullPointerException(
					"Project layout incongruent - Resource missing"
							+ description);
		}
	}

	public String getSynchronizedUniverseString() {

		return "";
	}

	public System getSystem() {
		return system;
	}

	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public Language getText() {

		return activeLanguage;
	}

	/**
	 * Instantiates a new universe.
	 * 
	 * @param resources
	 * @param s
	 */
	public void init(final Resource[] resources) {

		try {
			setupFrame();
		} catch (final Exception e) {
			e.printStackTrace();
			// System.exit(1);
		}

	}


	

	public void input(final InputEvent e) {

		final SortableLayerArray depthList = new SortableLayerArray(
				SortableLayerArray.COMPARE_DEPTH_HIGHEST_ABOVE);
		for (final Layer l : layerMap.values()) {
			depthList.add(l);
		}

		boolean wasCatchedAbove = false;
		for (final Layer l : depthList) {

			if (l.getGUI().input(e, wasCatchedAbove)) {

				wasCatchedAbove = true;
			}
		}
	}

	/**
	 * Checks if is busy.
	 * 
	 * @return the busy
	 */
	public boolean isBusy() {

		return busy;
	}

	public void output(final Graphics g) {
		// Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
		// backgroundColor.b, 1);
		Gdx.gl.glClearColor(5 / 255F, 70 / 255F, 70 / 255F, 255 / 255F);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		final SortableLayerArray depthList = new SortableLayerArray(
				SortableLayerArray.COMPARE_DEPTH_HIGHEST_BELOW);
		for (final Layer l : layerMap.values()) {
			depthList.add(l);
		}

		for (final Layer l : depthList) {

			l.getGUI().output(g);
		}

	}

	public void removeBusyListener(final InterruptionListener l) {

		BusyArray.removeValue(l, true);
	}

	public void removeLayer(final String label) {

		layerMap.removeKey(label);
	}

	public boolean saveStateToConfig() {

		try {
			final Preferences p = Gdx.app.getPreferences("config");
			p.putString("lang", getText().getProperty("languageShort"));
			p.putString("name", getPersonalPlayerName());
			// p.putString("bgcolor", backgroundColor);
			p.putString("table", getLayer(Universe.LayerBackGround).getModel()
					.toString());
			return true;
		} catch (final Exception e2) {
			e2.printStackTrace();
			return false;
		}
	}

	public void setBusy(final boolean busy) {

		if (busy) {
			getNetwork().clearUpdateQueue();
		}
		for (final InterruptionListener l : BusyArray) {
			l.setInterrupted(busy);
		}
		this.busy = busy;
	}

	public void setLanguage(final Language lang) {

		activeLanguage = lang;
	}

	public void setMenu(final Menu menu) {
		this.menu = menu;
	}

	public void setParent(final TableEngine parent) {
		this.tableEngine = parent;
	}

	public void setPersonalPlayerName(final String name) {

		this.name = name;
	}

	public void setPersonalView(final int viewNo) {

		view = viewNo;
	}

	public void setSystem(final System system) {
		this.system = system;
	}

	private void setupFrame() {

		Gdx.graphics.setTitle(getText().getProperty("game"));

		// Set Icon to "/resources/icon32.png"

		network = new PlainNetwork();
		network.initialize(this);

		// initialize layers

		menu = new Menu();
		system = new System(this);

		

		getLayer(Universe.LayerMenu).getModel().addObject(menu);
		// Gdx.graphics.setTitle(getText().getProperty("game"));

	}

	/**
	 * Load language LANG-files (which are ini-files).
	 * 
	 * @param langList
	 */
	private void setupLanguages(final Language[] langList) {

		// Add basic english language
		final Language engl = new BasicDefaultLanguage();
		languageMap.put(Locale.ENGLISH, engl);

		if (langList != null) {

			for (final Language lang : langList) {
				boolean isAlreadyInMap = false;
				for (final Language alreadyinMap : languageMap.values()) {
					if (lang.locale.getLanguage().equals(
							alreadyinMap.locale.getLanguage())) {
						isAlreadyInMap = true;
						alreadyinMap.putAll(lang);
					}
				}
				if (!isAlreadyInMap) {
					languageMap.put(lang.locale, lang);
					if (lang.locale.equals(Locale.getDefault())) {
						setLanguage(lang);
					}
				}
			}
			final Language defaults = getLanguage(Locale.ENGLISH);
			for (final Language l : getLanguageMap().values()) {
				l.setDefault(defaults);
			}
		}

		if (getText() == null) {
			setLanguage(languageMap.getValue(Locale.ENGLISH));
		}
	}

	private void setupLoaders(final UniverseLoader[] loaders) {

		if (loaders != null) {
			for (final UniverseLoader l : loaders) {
				l.loadObjectsToUniverse(this);
			}
			if ((startBGLayer != null) && (startBGLayer != "")) {
				getLayer(Universe.LayerBackGround).getModel()
						.removeAllObjects();
				getLayer(Universe.LayerBackGround).getModel().addObjects(
						startBGLayer);
			}
		}
	}

	private void setupMagnifier(final Magnifier m) {

		// getLayer(Universe.LayerMagnifier).getModel().addObject(m);
		magnifier = new NullMagnifier();
	}
}
