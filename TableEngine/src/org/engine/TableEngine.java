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

public class TableEngine implements ApplicationListener, Synchronizable,
		NetworkListener {

	public static final boolean SHOW_UI = true;
	public static final boolean SHOW_UNIVERSE = false;

	private OrthographicCamera camera;
	private Graphics g;
	private InputGenerator i;

	public Skin uiSkin;
	public String uiStyleName;

	private float savedHeight;
	private float savedWidth;

	private boolean showUI = false;

	private Stage ui;
	public Stage settingsScreen = null;

	private ArrayMap<String, Resource> resourceMap = new ArrayMap<String, Resource>();
	private BasicResource basicResource;

	private Array<Layer> layerList = new Array<Layer>();
	private Array<SyncProperty> syncLayerList = new Array<SyncProperty>();
	private SortableLayerArray outputLayerList = new SortableLayerArray(
			SortableLayerArray.COMPARE_DEPTH_HIGHEST_ABOVE);
	private SortableLayerArray inputLayerList = new SortableLayerArray(
			SortableLayerArray.COMPARE_DEPTH_HIGHEST_BELOW);
	private LongProperty objID = new LongProperty("OBJID", TABLE_ENGINE_TAG, 0L);

	public Preferences preferences;
	public final static String PREFERENCES_LOCATION = "TableEngine";

	public Array<String> debugOrderList = new Array<String>();
//	public boolean debugMode = false;
	private Language defLang;
	private Language language;
	public Network net;

	public static final String OBJECT_LAYER = "OBJ";
	public static final String POPUP_LAYER = "POP";
	public static final String MENU_LAYER = "MENU";
	public Menu menu = new Menu();

	public static final String TABLE_ENGINE_TAG = "T";
	public static final int ANIMATION_LIMIT = 5;
	public static final int CHAT_LIMIT = 10;
	public static final int PLAYER_LIMIT = 16;

	private StringArrayProperty animationList = new StringArrayProperty(
			"ANIMATIONS", TABLE_ENGINE_TAG, new Array<String>(ANIMATION_LIMIT,
					ANIMATION_LIMIT));
	private Array<String> performedAnimations = new Array<String>();
	public StringArrayProperty banList = new StringArrayProperty("BANS",
			TABLE_ENGINE_TAG, new Array<String>());
	public StringArrayProperty chatList = new StringArrayProperty("CHATS",
			TABLE_ENGINE_TAG, new Array<String>(CHAT_LIMIT, CHAT_LIMIT));
	private PlayerArrayProperty playerList = new PlayerArrayProperty("PLAYERS",
			TABLE_ENGINE_TAG, new Array<Player>(PLAYER_LIMIT, PLAYER_LIMIT));

	private int waitingForSend = 0;
	public static final int REQUEST_LOG_LIMIT = 10;
	public Array<String> requestLog = new Array<String>(REQUEST_LOG_LIMIT,
			REQUEST_LOG_LIMIT);
	public Array<ReplyCommand> replyQueue = new Array<ReplyCommand>();
	private boolean blockIncomingNetworkTraffic = false;
	private boolean rememberGotBlocked = false;
	private boolean waitingForIncomingNetworkTraffic = false;
	private long lastReceiveTimeStamp;

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
		// ENTRY POINT FOR START SCREEN
		// setUI(loadStartScreen(), SHOW_UI);

		// GDX REMOTE
		// RemoteInput ri = new RemoteInput();
		// ri.setInputProcessor(i);
		// setIO(false);
	}

	private void loadResources() {

		// Load game from TableEngineDefinition class
		TableEngineDefinition def = loadGame();
		if (def != null) {
			try {

				// Add Basic resource
				basicResource = new BasicResource();
				resourceMap.put(BasicResource.DESCRIPTION_ID, basicResource);
				basicResource.initialize(this);

				// Add additional resources
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
				
				// Load skin and style
				Skin s = resourceMap.getValue(BasicResource.DESCRIPTION_ID)
						.getSkin(BasicResource.DESCRIPTION_ID);
				String sn = "default";
				// If available overwrite with TableEngineDefinition
				if (def.getUISkin() != null) {
					s = def.getUISkin();
				}
				if (def.getUIStyleName() != null) {
					sn = def.getUIStyleName();
				}
				setStyle(s, sn);
				
				// Load Layers
				// Add Basic Layers and initialize Menu
				SyncLayer objectLayer = new SyncLayer(new TransformGUI(), OBJECT_LAYER,
						-100, true);
				objectLayer.initialize(this);
				syncLayerList.add(new SyncProperty(OBJECT_LAYER, TABLE_ENGINE_TAG, objectLayer));
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
					Array<MenuItem> mia = new Array<MenuItem>(def.getMenuItems());
					menu.addMenuItems(mia);
				}
				if (def.getLayers() != null) {
					for (Layer l : def.getLayers()) {
						l.initialize(this);
						layerList.add(l);
						if (Synchronizable.class.isAssignableFrom(l.getClass())) {
							syncLayerList.add(new SyncProperty(l.label.get(),
									TABLE_ENGINE_TAG, (Synchronizable) l));
						}
					}
				}
				outputLayerList.addAll(layerList);
				inputLayerList.addAll(layerList);
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
							switchUI(SHOW_UNIVERSE);
						}
					});
					t.add(startUniverse).expandX().fill();
					ui.addActor(t);
				}

				// Apply other stuff from definitions class
				setFullScreen(def.isFullScreen());
				settingsScreen = def.getSettingsStage();
				// Finally set style again for all the added Skinnable classes.
				setStyle(s, sn);

			} catch (Exception e) {

				// If something went wrong load an empty Universe showing just
				// an error message!
				// main = new NullUniverse(this);
				Gdx.app.log("EXCEPTION CAUGHT", e.getMessage());
				e.printStackTrace();
			}
		}
		i = new InputGenerator(this);

	}

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

	@Override
	public void dispose() {
		g.dispose();
		ui.dispose();
	}

	public Stage getUI() {
		return ui;
	}

	public boolean isShowing() {

		return showUI;
	}

	/**
	 * To be overwritten by actual game. The returned TableEngineDefinition
	 * class must provide the necessary resources and returns the programs entry
	 * point {@link TableEngineDefinition}.
	 * 
	 * @return the table engine definition
	 */
	protected TableEngineDefinition loadGame() {
		
		return TableEngineDefinition.DefaultTableEngineDefinition.getStandardDef();
	}

	@Override
	public void pause() {
		preferences.flush();
	}

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
	}

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
						setPropertiesFromInformation(Information.StringToInformations(c.s));
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

	private void checkPlayerRegistration() {

		String id = preferences.getString("playerID");
		String content = preferences.getString("playerID") + ";"
				+ preferences.getString("playerName") + ";"
				+ preferences.getString("playerAvatar") + ";"
				+ TransformView.fromTableEngineToString(this);
		if (playerList.contains(id)) {
			if (!playerList.getByID(id).toString().equals(content)) {
				playerList.getByID(id).fromString(content);
				playerList.setFlagged(true);
				requestSend();
			}
		} else {
			Player p = new Player();
			p.fromString(content);
			playerList.get().add(p);
			playerList.setFlagged(true);
			requestSend();
		}
	}

	@Override
	public void resize(final int width, final int height) {
		camera.setToOrtho(false, width, height);
		g.setProjectionMatrix(camera.combined);
		ui.setViewport(width, height, false);
		for (Layer l : layerList) {
			for (Interactable i : l.a) {
				if (Skinnable.class.isAssignableFrom(i.getClass())) {
					((Skinnable) (i)).adaptToScreenSize(width, height);
				}
			}
		}
	}

	@Override
	public void resume() {
		preferences = Gdx.app.getPreferences("TableEngine");
	}

	public void setUI(Stage ui) {

		this.ui = ui;
	}

	public void setUI(final Stage ui, final boolean showUI) {

		this.ui = ui;
		switchUI(showUI);
	}

	public void switchUI(final boolean showUI) {

		if (!showUI) {
			// SET TO UNIVERSE VIEW
			Gdx.input.setInputProcessor(i);
			Gdx.graphics.setContinuousRendering(false);
		} else {
			// SET TO USER INTERFACE STAGE VIEW
			Gdx.input.setInputProcessor(getUI());
			Gdx.graphics.setContinuousRendering(true);
			getUI().setViewport(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(), false);
		}
		this.showUI = showUI;

	}

	public void setFullScreen(boolean fullScreen) {

		if (Gdx.graphics.supportsDisplayModeChange()) {
			DisplayMode max = null;
			for (final DisplayMode d : Gdx.graphics.getDisplayModes()) {
				if ((max == null) || (max.width < d.width)
						|| (max.width == d.width && max.height < d.height)) {
					max = d;
				}
			}
			Gdx.graphics.setDisplayMode(max.width, max.height, fullScreen);
		}
	}

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

	public Array<Property<?>> getProperties() {

		Array<Property<?>> propertyArray = new Array<Property<?>>(new Property<?>[] { animationList, banList, chatList,
				playerList });

		for (SyncProperty p : syncLayerList) {

			propertyArray.add(p);
		}
		propertyArray.add(objID);
		return propertyArray;
	}

	@Override
	public Array<Property<?>> getPropertiesFlaggedOnly() {

		Array<Property<?>> propertyArray = new Array<Property<?>>();
		Property<?>[] ap = new Property[] { animationList, banList, chatList,
				playerList, objID };
		for (Property<?> p : ap) {

			if (p.isFlagged()) {
				propertyArray.add(p);
			}
		}

		for (SyncProperty p : syncLayerList) {

			if (p.isFlagged()) {
				propertyArray.add(p);
			}
		}
		return propertyArray;
	}

	public void addAnimation(String animation) {

		animationList.get().add(animation);
		performedAnimations.add(animation);
	}
	
	public void addChatMessage(String string) {

		animationList.get().add(string);
	}

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

	public Layer getLayer(String label) {

		for (Layer l : layerList) {
			if (l.label.get().equals(label)) {
				return l;
			}
		}
		return null;
	}

	public String getData(String resource, String name) {
		return resourceMap.getValue(resource).getData(name);
	}

	public String getDescription() {
		return "TABLE_ENGINE";
	}

	public TextureRegion getImage(String resource, String name) {
		return resourceMap.getValue(resource).getImage(name);

	}

	public Sound getSound(String resource, String name) {
		return resourceMap.getValue(resource).getSound(name);
	}

	public Skin getSkin(String resource, String name) {
		return resourceMap.getValue(resource).getSkin(name);
	}

	public void setLanguage(Locale locale) {

		language.clear();
		language.setDefault(defLang);
		for (Resource r : resourceMap.values()) {
			if (r.getLanguage(locale) != null) {
				language.putAll(r.getLanguage(locale));
			}
		}
	}

	public String getText(String key) {

		return language.getProperty(key);
	}

	public void resetDatabases() {
		for (Resource r : resourceMap.values()) {
			r.resetDatabases();
		}
	}

	public static void main(String[] args) {

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "TableEngine";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;
		
		TableEngine t = new TableEngine();
		new LwjglApplication(t, cfg);
		if (args[0] != null) {
			if (args[0].equals("debug")) {
				new DebugWindow(t).open();
			}
		}
		System.out.println(t.getPrefBoolean("debug") + " debug");
	}

	public String getPrefString(String key) {
		return preferences.getString(key, "String not found");
	}

	public int getPrefInteger(String key) {
		return preferences.getInteger(key, -1);
	}

	public float getPrefFloat(String key) {
		return preferences.getFloat(key, -1.0F);
	}

	public long getPrefLong(String key) {
		return preferences.getLong(key, -1L);
	}

	public boolean getPrefBoolean(String key) {
		return preferences.getBoolean(key, false);
	}

	public boolean isPrefAvailable(String key) {
		return preferences.contains(key);
	}

	private void checkKey(String key) {
		if (!preferences.contains(key)) {
			Gdx.app.error(
					"Preferences",
					"Key not available, make sure the initially loaded Resource classes provide access to a default value for this key");
		}
	}

	public void putString(String key, String val) {
		checkKey(key);
		preferences.putString(key, val);
	}

	public void putPrefInteger(String key, int val) {
		checkKey(key);
		preferences.putInteger(key, val);
	}

	public void putPrefFloat(String key, float val) {
		checkKey(key);
		preferences.putFloat(key, val);
	}

	public void putPrefLong(String key, long val) {
		checkKey(key);
		preferences.putLong(key, val);
	}

	public void putPrefBoolean(String key, boolean val) {
		checkKey(key);
		preferences.putBoolean(key, val);
	}

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

	public boolean gotBlockedIncomingNetworkTrafficInMeantime() {

		return rememberGotBlocked;
	}

	public void resetRememberBlock() {

		rememberGotBlocked = false;
	}

	public boolean isBlockingIncomingNetworkTraffic() {

		return blockIncomingNetworkTraffic;
	}

	public enum RequestCommand {

		Info, Key, Send, Receive, TimeStamp, Debug;
	}

	public void requestSend() {

		final TableEngine t = this;
		if (waitingForSend == 0) {

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

	public void requestReceive() {

		if (!isBlockingIncomingNetworkTraffic()) {
			net.requestTimestamp(this);
			waitingForIncomingNetworkTraffic = true;
		}
	}

	@Override
	public void error(Throwable t, String action) {

		Gdx.app.error("SERVER ERROR: " + action, t.getMessage());
	}

	@Override
	public void info(Array<Slot> slotArray) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Info;
		rc.a = slotArray;
		replyQueue.add(rc);
	}

	@Override
	public void key(boolean validKey) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Key;
		rc.b = validKey;
		replyQueue.add(rc);
	}

	@Override
	public void receive(long lastTimeStamp, String response) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Receive;
		rc.s = response;
		rc.l = lastTimeStamp;
		replyQueue.add(rc);
	}

	@Override
	public void send(long lastTimeStamp, boolean sendSuccess) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Send;
		rc.l = lastTimeStamp;
		rc.b = sendSuccess;
		replyQueue.add(rc);
	}

	@Override
	public void timestamp(long lastTimeStamp) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.TimeStamp;
		rc.l = lastTimeStamp;
		replyQueue.add(rc);
	}

	@Override
	public void debug(String response) {

		ReplyCommand rc = new ReplyCommand();
		rc.type = RequestCommand.Debug;
		rc.s = response;
		replyQueue.add(rc);
	}

	public String getNewObjectID(Class<?> c) {

		objID.set(objID.get() + 1);
		return c.getCanonicalName() + "-" + objID.get();
	}

	public class ReplyCommand {

		public RequestCommand type;
		public String s;
		public boolean b;
		public long l;
		public Array<Slot> a;
	}
}