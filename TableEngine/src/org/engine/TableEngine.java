package org.engine;

import java.io.IOException;
import java.util.Locale;

import org.engine.Layer.SyncLayer;
import org.engine.Player.PlayerArrayProperty;
import org.engine.debug.DebugWindow;
import org.engine.debug.TestInteractable;
import org.engine.gui.PlainGUI;
import org.engine.gui.TransformGUI;
import org.engine.gui.TransformGUI.TransformView;
import org.engine.gui.input.InputEvent;
import org.engine.gui.input.InputGenerator;
import org.engine.gui.output.Graphics;
import org.engine.language.Language;
import org.engine.menu.Menu;
import org.engine.menu.MenuItem;
import org.engine.network.Network;
import org.engine.network.Network.NetworkListener;
import org.engine.network.Server.Slot;
import org.engine.network.ServerNetwork;
import org.engine.property.Information;
import org.engine.property.LongProperty;
import org.engine.property.Property;
import org.engine.property.Property.Flag;
import org.engine.property.SyncProperty;
import org.engine.resource.BasicResource;
import org.engine.resource.Resource;
import org.engine.utils.Array;
import org.engine.utils.ArrayMap;
import org.engine.utils.SortableLayerArray;
import org.engine.utils.StringArrayProperty;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * <code>TableEngine</code> is the main computing class. TableEngine is capable
 * of showing objects in a 2D environment and manages interaction and
 * synchronization via Internet of these. Therefore a system of
 * String-convertible {@link Property Properties} is supervised by a network,
 * that checks whether one of these has been flagged as changed. Consequently
 * they are turned into Strings and uploaded to a <code>PHP-Server</code>. User
 * IO is generally updated only on interaction, decreasing CPU usage.</br>
 * 
 * In order to write a game, <code>TableEngine</code> is to be extended and the
 * {@link #loadGame()} method overwritten. It is recommended, that all game
 * resources are packed into a {@link Resource} object, that is provided by the
 * {@link TableEngineDefinition} from <code>loadGame()</code>.</br>
 * 
 * The actual entry point for the game will be the {@link Stage} provided by
 * {@link TableEngineDefinition#getEntryStage()}.</br>
 * 
 * For more information about IO see {@link #input(InputEvent)} and
 * {@link #output(Graphics)}.
 * 
 */
public class TableEngine implements ApplicationListener, Synchronizable,
		NetworkListener {

	public static final boolean SHOW_UI = true;
	public static final boolean SHOW_TABLE = false;
	private boolean showUI = false;

	// IO CLASSES
	private OrthographicCamera camera;
	private Graphics g;
	private float savedHeight;
	private float savedWidth;
	private InputGenerator i;

	// SKIN & STYLE CLASSES
	public Skin uiSkin;
	public String uiStyleName;

	// STAGES
	private Stage ui;
	public Stage settingsScreen = null;

	// RESOURCES
	private ArrayMap<String, Resource> resourceMap = new ArrayMap<String, Resource>();
	private BasicResource basicResource;

	// LAYERS
	private Array<Layer> layerList = new Array<Layer>();
	private Array<SyncProperty> syncLayerList = new Array<SyncProperty>();
	private SortableLayerArray outputLayerList = new SortableLayerArray(
			SortableLayerArray.COMPARE_DEPTH_HIGHEST_ABOVE);
	private SortableLayerArray inputLayerList = new SortableLayerArray(
			SortableLayerArray.COMPARE_DEPTH_HIGHEST_BELOW);
	public static final String OBJECT_LAYER = "OBJ";
	public static final String POPUP_LAYER = "POP";
	public static final String MENU_LAYER = "MENU";
	public Menu menu = new Menu();

	// PREFERENCES
	public Preferences preferences;
	public final static String PREFERENCES_LOCATION = "TableEngine";

	// DEBUG ORDERS
	public Array<String> debugOrderList = new Array<String>();

	// LANGUAGE
	private Language defLang;
	private Language language;

	// NETWORK
	public Network net;
	private int waitingForSend = 0;
	public static final int REQUEST_LOG_LIMIT = 10;
	public Array<String> requestLog = new Array<String>(REQUEST_LOG_LIMIT,
			REQUEST_LOG_LIMIT);
	public Array<ReplyCommand> replyQueue = new Array<ReplyCommand>();
	private boolean blockIncomingNetworkTraffic = false;
	private boolean rememberGotBlocked = false;
	private boolean waitingForIncomingNetworkTraffic = false;
	private long lastReceiveTimeStamp;

	// RIGID PROPERTIES
	public static final String TABLE_ENGINE_TAG = "T";
	private LongProperty objID = new LongProperty("OBJID", TABLE_ENGINE_TAG,
			Flag.NONE, 0L);
	public static final int ANIMATION_LIMIT = 5;
	public static final int CHAT_LIMIT = 10;
	public static final int PLAYER_LIMIT = 16;
	private StringArrayProperty animationList = new StringArrayProperty(
			"ANIMATIONS", TABLE_ENGINE_TAG, Flag.NONE, new Array<String>(
					ANIMATION_LIMIT, ANIMATION_LIMIT));
	private Array<String> performedAnimations = new Array<String>();
	public StringArrayProperty banList = new StringArrayProperty("BANS",
			TABLE_ENGINE_TAG, Flag.NONE, new Array<String>());
	public StringArrayProperty chatList = new StringArrayProperty("CHATS",
			TABLE_ENGINE_TAG, Flag.NONE, new Array<String>(CHAT_LIMIT,
					CHAT_LIMIT));
	private PlayerArrayProperty playerList = new PlayerArrayProperty("PLAYERS",
			TABLE_ENGINE_TAG, Flag.NONE, new Array<Player>(PLAYER_LIMIT,
					PLAYER_LIMIT));
	private Property<?>[] rigidProperties = new Property[] { objID, animationList,
			banList, chatList, playerList };
	Array<Property<?>> propertyArray = new Array<Property<?>>(rigidProperties);
	Array<Property<?>> flaggedPropertyArray = new Array<Property<?>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {

		savedWidth = Gdx.graphics.getWidth();
		savedHeight = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, savedWidth, savedHeight);

		Gdx.graphics.setContinuousRendering(false);

		g = new Graphics();

		loadResources();

		setUI(ui, SHOW_UI);
	}

	/**
	 * Load resources. Called during {@link #create()} method. Initiates the
	 * {@link TableEngineDefinition} provided by {@link #loadGame()}, loading
	 * all {@link Resource Resources} and starting the game ({@link Stage} of
	 * {@link TableEngineDefinition#getEntryStage()}.
	 */
	private void loadResources() {

		// Load game from TableEngineDefinition class
		TableEngineDefinition def = loadGame();
		if (def != null) {
			try {

				// Add basic resource -> contained in jar!
				basicResource = new BasicResource();
				resourceMap.put(BasicResource.DESCRIPTION_ID, basicResource);
				basicResource.initialize(this);

				// Add additional resources from TableEngineDefinition = game
				// specific resources
				if (def.getResourceData() != null) {

					for (final Resource r : def.getResourceData()) {

						resourceMap.put(r.getID(), r);
						r.initialize(this);
					}
				}

				// Load Settings
				preferences = Gdx.app.getPreferences(PREFERENCES_LOCATION);
				if (preferences.get().size() == 0) {

					// No settings saved before, load default settings
					for (Resource r : resourceMap.values()) {
						preferences.put(r.getDefaultPreferences().get());
					}
				}

				// Load default Language
				defLang = new Language();
				language = new Language();
				for (Resource r : resourceMap.values()) {
					defLang.putAll(r.getLanguage(Locale.ENGLISH));
				}
				setLanguage(Locale.forLanguageTag(getPrefString("language")));

				// Load network
				if (def.getNetwork() != null) {
					net = def.getNetwork();
					net.initialize(this);
				} else {
					// initialize standard PHP-Server network
					ServerNetwork snet = new ServerNetwork();
					snet.initialize(this);
					snet.url = getPrefString("url");
					snet.key = getPrefString("key");
					snet.keyID = getPrefInteger("gameID");
					snet.playerID = getPrefString("playerID");
					snet.playerNickName = getPrefString("playerName");
					snet.playerPassword = getPrefString("playerPassword");
					net = snet;
				}

				// Load basic skin and style
				Skin s = resourceMap.getValue(BasicResource.DESCRIPTION_ID)
						.getSkin(BasicResource.DESCRIPTION_ID);
				String sn = "default";
				// If available overwrite with TableEngineDefinition = game skin
				if (def.getUISkin() != null) {
					s = def.getUISkin();
				}
				if (def.getUIStyleName() != null) {
					sn = def.getUIStyleName();
				}
				setStyle(s, sn);

				// Load Layers
				// Add basic Layers and initialize Menu
				SyncLayer objectLayer = new SyncLayer(new TransformGUI(),
						OBJECT_LAYER, -100, true);
				objectLayer.initialize(this);
				syncLayerList.add(new SyncProperty(OBJECT_LAYER,
						TABLE_ENGINE_TAG, Flag.NONE, objectLayer));
				layerList.add(objectLayer);
				Layer popupLayer = new Layer(new PlainGUI(), POPUP_LAYER, -90,
						true);
				popupLayer.initialize(this);
				layerList.add(popupLayer);
				Layer menuLayer = new Layer(new PlainGUI(), MENU_LAYER, -80,
						true);
				menuLayer.initialize(this);
				layerList.add(menuLayer);
				menuLayer.addInteractable(menu);
				if (def.getMenuItems() != null) {
					Array<MenuItem> mia = new Array<MenuItem>(
							def.getMenuItems());
					menu.addMenuItems(mia);
				}
				// add additional Layers
				if (def.getLayers() != null) {
					for (Layer l : def.getLayers()) {
						l.initialize(this);
						layerList.add(l);
						if (Synchronizable.class.isAssignableFrom(l.getClass())) {
							syncLayerList.add(new SyncProperty(l.label.get(),
									TABLE_ENGINE_TAG, Flag.NONE,
									(Synchronizable) l));
						}
					}
				}
				// put Layers to differently sorted lists
				outputLayerList.addAll(layerList);
				inputLayerList.addAll(layerList);
				for (SyncProperty sl : syncLayerList) {
					propertyArray.add(sl);
				}
				objectLayer.addInteractable(new TestInteractable());
				objectLayer.addInteractable(new TestInteractable());

				// Load entry stage class if available
				if (def.getEntryStage() != null) {
					ui = def.getEntryStage();
				} else {
					// Add basic success Screen
					ui = new Stage();
					Table t = new Table();
					t.setFillParent(true);
					t.add(new Label(
							"TableEngine successfully loaded... No Entry Stage found...",
							uiSkin)).expand().center();
					t.row();
					TextButton startUniverse = new TextButton(
							"start game class", uiSkin);
					startUniverse.addListener(new ClickListener() {

						@Override
						public void clicked(
								com.badlogic.gdx.scenes.scene2d.InputEvent event,
								float x, float y) {

							super.clicked(event, x, y);
							switchUI(SHOW_TABLE);
						}
					});
					t.add(startUniverse).expandX().fill();
					ui.addActor(t);
				}

				// Apply other stuff from definitions class
				if (def.isFullScreen()) {

					setFullScreen(true);
					resize(Gdx.graphics.getDesktopDisplayMode().width,
							Gdx.graphics.getDesktopDisplayMode().height);
				}
				settingsScreen = def.getSettingsStage();
				// Finally set style again for all the added Skinnable classes.
				setStyle(s, sn);
			} catch (Exception e) {

				Gdx.app.error("EXCEPTION CAUGHT DURING LOADING: ",
						e.getMessage()
								+ "\ni have no clue what i am doing... you"
								+ "'re screwed, so utterly utterly screwed.");
				e.printStackTrace();
			}
		}
		i = new InputGenerator(this);

	}

	/**
	 * Sets the style.
	 * 
	 * @param uiSkin
	 *            the ui skin
	 * @param uiStyleName
	 *            the ui style name
	 */
	private void setStyle(Skin uiSkin, String uiStyleName) {

		this.uiSkin = uiSkin;
		this.uiStyleName = uiStyleName;
		for (Layer l : layerList) {
			for (Interactable i : l.a) {
				if (Skinnable.class.isAssignableFrom(i.getClass())) {
					((Skinnable) i).style(uiSkin, uiStyleName);
				}
			}
		}
		if (ui != null && Skinnable.class.isAssignableFrom(ui.getClass())) {
			((Skinnable) ui).style(uiSkin, uiStyleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	@Override
	public void dispose() {
		g.dispose();
		ui.dispose();
	}

	/**
	 * Gets the ui.
	 * 
	 * @return the ui
	 */
	public Stage getUI() {
		return ui;
	}

	/**
	 * Checks if is showing.
	 * 
	 * @return true, if is showing
	 */
	public boolean isShowing() {

		return showUI;
	}

	/**
	 * To be overwritten by actual game. The returned TableEngineDefinition
	 * class must provide the necessary resources and returns the programs entry
	 * point {@link TableEngineDefinition}. Therefore this is the most important
	 * and usually only interface to the game content.</br>
	 * 
	 * The intended way of writing a game is to extend {@link TableEngine} and
	 * overwrite only this method by a <code>TableEngineDefinition</code>.
	 * <code>TableEngine</code> will load the provided content (see
	 * {@link #loadResources()}) and start by showing the Stage provided by
	 * {@link TableEngineDefinition#getEntryStage()}.
	 * 
	 * @return the table engine definition
	 */
	protected TableEngineDefinition loadGame() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	@Override
	public void pause() {
		preferences.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#render()
	 */
	@Override
	public void render() {

		while (debugOrderList.getSize() > 0) {

			debugOrder(debugOrderList.get(0));
			debugOrderList.removeIndex(0);
		}

		checkPlayerRegistration();
		checkNetworkReplies();

		if (!showUI) {

			g.deltaTime = Gdx.graphics.getDeltaTime();
			g.setColor(Color.valueOf(getPrefString("bgColor")));
			g.fillRect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			output(g);
			
		} else {
			Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			ui.act();
			ui.draw();
		}

		g.endDrawing();

		if (isFlagged()) {
			requestSend();
		}
	}

	public boolean isFlagged() {

		for (Property<?> p : getProperties()) {
			if (p.flag() != Flag.NONE) {
				return true;
			}
		}
		return false;
	}

	public void resetFlags() {

		for (Property<?> p : getProperties()) {
			p.setFlag(Flag.NONE);
		}
	}

	/**
	 * Check network replies.
	 */
	private void checkNetworkReplies() {

		while (replyQueue.getSize() > 0) {

			ReplyCommand c = replyQueue.popFirst();
			if (c.type == RequestCommand.TimeStamp) {

				if (c.l != lastReceiveTimeStamp
						&& !gotBlockedIncomingNetworkTrafficInMeantime()) {

					net.requestReceive(this);
					waitingForIncomingNetworkTraffic = true;
				}
			}
			if (c.type == RequestCommand.Receive) {

				Gdx.app.log("RECEIVE", c.s);
				try {
					if (!gotBlockedIncomingNetworkTrafficInMeantime()) {
						setPropertiesFromInformation(Information
								.StringToInformations(c.s));
					} else {
						waitingForIncomingNetworkTraffic = false;
						if (!isBlockingIncomingNetworkTraffic()) {
							resetRememberBlock();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				lastReceiveTimeStamp = c.l;
			}
			if (c.type == RequestCommand.Key) {

				Gdx.app.log("KEY", "valid:" + c.b);
			}
			if (c.type == RequestCommand.Send) {

				Gdx.app.debug("SEND", "" + c.b);
			}
			if (c.type == RequestCommand.Debug) {

				Gdx.app.debug("DEBUG", c.s);
			}
		}
	}

	/**
	 * Check player registration.
	 */
	private void checkPlayerRegistration() {

		String id = preferences.getString("playerID");
		String content = preferences.getString("playerID") + ";"
				+ preferences.getString("playerName") + ";"
				+ preferences.getString("playerAvatar") + ";"
				+ TransformView.fromTableEngineToString(this);
		if (playerList.contains(id)) {
			if (!playerList.getByID(id).toString().equals(content)) {
				playerList.getByID(id).fromString(content);
				playerList.setFlag(Flag.UPDATE);
				requestSend();
			}
		} else {
			Player p = new Player();
			p.fromString(content);
			playerList.get().add(p);
			playerList.setFlag(Flag.UPDATE);
			requestSend();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	@Override
	public void resize(final int width, final int height) {
		camera.setToOrtho(false, width, height);
		g.setProjectionMatrix(camera.combined);
		ui.getViewport().setWorldSize(width, height);
		for (Layer l : layerList) {
			for (Interactable i : l.a) {
				if (Skinnable.class.isAssignableFrom(i.getClass())) {
					((Skinnable) (i)).adaptToScreenSize(width, height);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#resume()
	 */
	@Override
	public void resume() {
		preferences = Gdx.app.getPreferences("TableEngine");
	}

	/**
	 * Sets the ui.
	 * 
	 * @param ui
	 *            the new ui
	 */
	public void setUI(Stage ui) {

		this.ui = ui;
	}

	/**
	 * Sets the ui.
	 * 
	 * @param ui
	 *            the ui
	 * @param showUI
	 *            the show ui
	 */
	public void setUI(final Stage ui, final boolean showUI) {

		this.ui = ui;
		switchUI(showUI);
	}

	/**
	 * Switch ui.
	 * 
	 * @param showUI
	 *            the show ui
	 */
	public void switchUI(final boolean showUI) {

		if (!showUI) {
			// SET TO UNIVERSE VIEW
			Gdx.input.setInputProcessor(i);
			Gdx.graphics.setContinuousRendering(false);
		} else {
			// SET TO USER INTERFACE STAGE VIEW
			Gdx.input.setInputProcessor(getUI());
			Gdx.graphics.setContinuousRendering(true);
			getUI().getViewport().setWorldSize(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight());
		}
		this.showUI = showUI;

	}

	/**
	 * Sets the full screen.
	 * 
	 * @param fullScreen
	 *            the new full screen
	 */
	public void setFullScreen(boolean fullScreen) {

		if (Gdx.graphics.supportsDisplayModeChange()) {
			DisplayMode max = null;
			for (final DisplayMode d : Gdx.graphics.getDisplayModes()) {
				if ((max == null) || (max.width < d.width)
						|| (max.width == d.width && max.height < d.height)) {
					max = d;
				}
			}
			Gdx.graphics.setDisplayMode(max);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.Synchronizable#setPropertiesFromInformation(org.engine.utils
	 * .Array)
	 */
	public void setPropertiesFromInformation(Array<Information> ai) {

		Property<?>[] ap = new Property[] { animationList, banList, chatList,
				playerList, objID };
		for (Information i : ai) {

			for (Property<?> p : ap) {

				p.applySyncInfo(i);
			}

			for (SyncProperty p : syncLayerList) {

				p.applySyncInfo(i);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#getProperties()
	 */
	public Array<Property<?>> getProperties() {

		return propertyArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#getPropertiesFlagged()
	 */
	@Override
	public Array<Property<?>> getPropertiesFlagged() {

		flaggedPropertyArray.clear();
		for (Property<?> p : propertyArray) {

			if (p.flag() != Property.Flag.NONE) {
				flaggedPropertyArray.add(p);
			}
		}
		if (flaggedPropertyArray.getSize() > 0) {
			return flaggedPropertyArray;
		}
		return null;
	}

	/**
	 * Adds the animation.
	 * 
	 * @param animation
	 *            the animation
	 */
	public void addAnimation(String animation) {

		animationList.get().add(animation);
		performedAnimations.add(animation);
	}

	/**
	 * Adds the chat message.
	 * 
	 * @param string
	 *            the string
	 */
	public void addChatMessage(String string) {

		animationList.get().add(string);
	}

	/**
	 * Debug order.
	 * 
	 * @param order
	 *            the order
	 */
	private void debugOrder(String order) {

		if (order.startsWith("Skin")) {
			String path = order.substring(order.indexOf(":") + 1);
			setStyle(new Skin(Gdx.files.internal(path)), uiStyleName);
		}
		if (order.startsWith("Style")) {
			String styleName = order.substring(order.indexOf(":") + 1);
			setStyle(uiSkin, styleName);
		}
		if (order.startsWith("Pack")) {
			try {
				BasicResource.doPack();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the layer.
	 * 
	 * @param label
	 *            the label
	 * @return the layer
	 */
	public Layer getLayer(String label) {

		for (Layer l : layerList) {
			if (l.label.get().equals(label)) {
				return l;
			}
		}
		return null;
	}

	/**
	 * Gets the data.
	 * 
	 * @param resource
	 *            the resource
	 * @param name
	 *            the name
	 * @return the data
	 */
	public String getData(String resource, String name) {
		return resourceMap.getValue(resource).getData(name);
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return "TABLE_ENGINE";
	}

	/**
	 * Gets the image.
	 * 
	 * @param resource
	 *            the resource
	 * @param name
	 *            the name
	 * @return the image
	 */
	public TextureRegion getImage(String resource, String name) {
		return resourceMap.getValue(resource).getImage(name);

	}

	/**
	 * Gets the sound.
	 * 
	 * @param resource
	 *            the resource
	 * @param name
	 *            the name
	 * @return the sound
	 */
	public Sound getSound(String resource, String name) {
		return resourceMap.getValue(resource).getSound(name);
	}

	/**
	 * Gets the skin.
	 * 
	 * @param resource
	 *            the resource
	 * @param name
	 *            the name
	 * @return the skin
	 */
	public Skin getSkin(String resource, String name) {
		return resourceMap.getValue(resource).getSkin(name);
	}

	/**
	 * Sets the language.
	 * 
	 * @param locale
	 *            the new language
	 */
	public void setLanguage(Locale locale) {

		language.clear();
		language.setDefault(defLang);
		for (Resource r : resourceMap.values()) {
			if (r.getLanguage(locale) != null) {
				language.putAll(r.getLanguage(locale));
			}
		}
	}

	/**
	 * Gets the text.
	 * 
	 * @param key
	 *            the key
	 * @return the text
	 */
	public String getText(String key) {

		return language.getProperty(key);
	}

	/**
	 * Reset databases.
	 */
	public void resetDatabases() {
		for (Resource r : resourceMap.values()) {
			r.resetDatabases();
		}
	}

	/**
	 * A main method for testing purposes. W
	 * 
	 * @param args
	 *            title width height debug
	 */
	public static void main(String[] args) {

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "TableEngine";
		cfg.width = 480;
		cfg.height = 320;

		TableEngine t = new TableEngine() {

			@Override
			protected TableEngineDefinition loadGame() {

				return TableEngineDefinition.DefaultTableEngineDefinition
						.getStandardDef();
			}
		};

		if (args.length > 2) {
			cfg.title = args[0];
			cfg.width = Integer.parseInt(args[1]);
			cfg.height = Integer.parseInt(args[2]);

			if (Boolean.parseBoolean(args[3])) {

				DebugWindow d = new DebugWindow(t);
				d.open();
			}
		}

		new LwjglApplication(t, cfg);
	}

	/**
	 * Gets the pref string.
	 * 
	 * @param key
	 *            the key
	 * @return the pref string
	 */
	public String getPrefString(String key) {
		return preferences.getString(key, "String not found");
	}

	/**
	 * Gets the pref integer.
	 * 
	 * @param key
	 *            the key
	 * @return the pref integer
	 */
	public int getPrefInteger(String key) {
		return preferences.getInteger(key, -1);
	}

	/**
	 * Gets the pref float.
	 * 
	 * @param key
	 *            the key
	 * @return the pref float
	 */
	public float getPrefFloat(String key) {
		return preferences.getFloat(key, -1.0F);
	}

	/**
	 * Gets the pref long.
	 * 
	 * @param key
	 *            the key
	 * @return the pref long
	 */
	public long getPrefLong(String key) {
		return preferences.getLong(key, -1L);
	}

	/**
	 * Gets the pref boolean.
	 * 
	 * @param key
	 *            the key
	 * @return the pref boolean
	 */
	public boolean getPrefBoolean(String key) {
		return preferences.getBoolean(key, false);
	}

	/**
	 * Checks if is pref available.
	 * 
	 * @param key
	 *            the key
	 * @return true, if is pref available
	 */
	public boolean isPrefAvailable(String key) {
		return preferences.contains(key);
	}

	/**
	 * Check key.
	 * 
	 * @param key
	 *            the key
	 */
	private void checkKey(String key) {
		if (!preferences.contains(key)) {
			Gdx.app.error(
					"Preferences",
					"Key not available, make sure the initially loaded Resource classes provide access to a default value for this key");
		}
	}

	/**
	 * Put string.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void putString(String key, String val) {
		checkKey(key);
		preferences.putString(key, val);
	}

	/**
	 * Put pref integer.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void putPrefInteger(String key, int val) {
		checkKey(key);
		preferences.putInteger(key, val);
	}

	/**
	 * Put pref float.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void putPrefFloat(String key, float val) {
		checkKey(key);
		preferences.putFloat(key, val);
	}

	/**
	 * Put pref long.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void putPrefLong(String key, long val) {
		checkKey(key);
		preferences.putLong(key, val);
	}

	/**
	 * Put pref boolean.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the val
	 */
	public void putPrefBoolean(String key, boolean val) {
		checkKey(key);
		preferences.putBoolean(key, val);
	}

	/**
	 * Input.
	 * 
	 * @param inputEvent
	 *            the input event
	 */
	public void input(InputEvent inputEvent) {

		boolean catched = false;
		for (Layer l : inputLayerList) {
			if (l.gui.get().input(inputEvent, catched)) {
				if (l.persistent.get() && !catched) {
					break;
				}
				catched = true;
			}
		}
	}

	/**
	 * Output.
	 * 
	 * @param g
	 *            the g
	 */
	public void output(Graphics g) {

		for (Layer l : outputLayerList) {
			l.gui.get().output(g);
		}
	}

	/*
	 * Blocking and flagging methods: if the engine wants to receive info from
	 * server, it performs a request (see @link(requestReceive()) and sets a
	 * flag (waitingForIncomingTraffic). Each time the user changes something on
	 * the table, the class should block incoming traffic until the user
	 * operation is done. This way a user operation is not written over by some
	 * info from server. As network communication works asynchronously, user
	 * interaction in the meantime must be registered (or remembered). There are
	 * 3 possible scenarios: Scenario 1: RECEIVE-START -> SET FLAG ! ... MOVE
	 * CURSOR -> BLOCK TRAFFIC ... CURSOR DONE -> UNBLOCK TRAFFIC ... RECEIVE
	 * ---> TRAFFIC WAS BLOCKED -> UNSET FLAG, RESET BLOCK
	 * 
	 * 
	 * Scenario 2: RECEIVE-START -> SET FLAG ! ... MOVE CURSOR -> BLOCK TRAFFIC
	 * ... RECEIVE ---> TRAFFIC WAS BLOCKED -> UNSET FLAG ... CURSOR DONE ->
	 * UNBLOCK TRAFFIC -> NO FLAG SET, RESET BLOCK
	 * 
	 * 
	 * Scenario 3: MOVE CURSOR -> BLOCK TRAFFIC ... RECEIVE-START -> DO NOTHING
	 * ... CURSOR DONE -> UNBLOCK TRAFFIC -> NO FLAG SET, RESET BLOCK
	 */
	/**
	 * Sets the block incoming network traffic.
	 * 
	 * @param blockIncomingNetworkTraffic
	 *            the new block incoming network traffic
	 */
	public void setBlockIncomingNetworkTraffic(
			final boolean blockIncomingNetworkTraffic) {

		if (blockIncomingNetworkTraffic) {
			rememberGotBlocked = true;
		} else {
			if (!waitingForIncomingNetworkTraffic) {
				resetRememberBlock();
			}
		}
		this.blockIncomingNetworkTraffic = blockIncomingNetworkTraffic;
	}

	/**
	 * Got blocked incoming network traffic in meantime.
	 * 
	 * @return true, if successful
	 */
	public boolean gotBlockedIncomingNetworkTrafficInMeantime() {

		return rememberGotBlocked;
	}

	/**
	 * Reset remember block.
	 */
	public void resetRememberBlock() {

		rememberGotBlocked = false;
	}

	/**
	 * Checks if is blocking incoming network traffic.
	 * 
	 * @return true, if is blocking incoming network traffic
	 */
	public boolean isBlockingIncomingNetworkTraffic() {

		return blockIncomingNetworkTraffic;
	}

	/**
	 * The Enum RequestCommand.
	 */
	public enum RequestCommand {

		/** The Info. */
		Info, /** The Key. */
		Key, /** The Send. */
		Send, /** The Receive. */
		Receive, /** The Time stamp. */
		TimeStamp, /** The Debug. */
		Debug;
	}

	/**
	 * Request send.
	 */
	public void requestSend() {

		final TableEngine t = this;
		if (waitingForSend == 0) {

			waitingForSend++;
			new Thread() {

				private int waitSendingMillis = 1000;

				@Override
				public void run() {

					long startStamp = TimeUtils.millis();
					int startInt = t.waitingForSend;
					// wait some seconds before sending because new SEND
					// commands may follow
					while ((TimeUtils.millis() - startStamp) < (waitSendingMillis)) {

						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
						// If there has been a new SEND Command reset the
						// waiting loop
						if (t.waitingForSend > startInt) {

							startStamp = TimeUtils.millis();
							startInt = t.waitingForSend;
						}
					}
					t.net.requestSend(t);
					t.requestLog.add("SEND");
					t.waitingForSend = 0;
				}
			}.start();
		} else {
			waitingForSend++;
		}

	}

	/**
	 * Request receive.
	 */
	public void requestReceive() {

		if (!isBlockingIncomingNetworkTraffic()) {
			net.requestTimestamp(this);
			waitingForIncomingNetworkTraffic = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.network.Network.NetworkListener#error(java.lang.Throwable,
	 * java.lang.String)
	 */
	@Override
	public void error(Throwable t, String action) {

		Gdx.app.error("SERVER ERROR: " + action, t.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.engine.network.Network.NetworkListener#info(org.engine.utils.Array)
	 */
	@Override
	public void info(Array<Slot> slotArray) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Info;
		rc.a = slotArray;
		replyQueue.add(rc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network.NetworkListener#key(boolean)
	 */
	@Override
	public void key(boolean validKey) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Key;
		rc.b = validKey;
		replyQueue.add(rc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network.NetworkListener#receive(long,
	 * java.lang.String)
	 */
	@Override
	public void receive(long lastTimeStamp, String response) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Receive;
		rc.s = response;
		rc.l = lastTimeStamp;
		replyQueue.add(rc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network.NetworkListener#send(long, boolean)
	 */
	@Override
	public void send(long lastTimeStamp, boolean sendSuccess) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Send;
		rc.l = lastTimeStamp;
		rc.b = sendSuccess;
		replyQueue.add(rc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network.NetworkListener#timestamp(long)
	 */
	@Override
	public void timestamp(long lastTimeStamp) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.TimeStamp;
		rc.l = lastTimeStamp;
		replyQueue.add(rc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Network.NetworkListener#debug(java.lang.String)
	 */
	@Override
	public void debug(String response) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Debug;
		rc.s = response;
		replyQueue.add(rc);
	}

	/**
	 * Gets the new object id.
	 * 
	 * @param c
	 *            the c
	 * @return the new object id
	 */
	public String getNewObjectID(Class<?> c) {

		objID.set(objID.get() + 1);
		return c.getCanonicalName() + "-" + objID.get();
	}

	/**
	 * The Class ReplyCommand.
	 */
	public class ReplyCommand {

		/** The type. */
		public RequestCommand type;

		/** The s. */
		public String s;

		/** The b. */
		public boolean b;

		/** The l. */
		public long l;

		/** The a. */
		public Array<Slot> a;
	}
}