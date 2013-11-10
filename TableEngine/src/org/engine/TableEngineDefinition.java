package org.engine;

import org.engine.menu.MenuItem;
import org.engine.network.Network;
import org.engine.network.TextNetwork;
import org.engine.resource.BasicResource;
import org.engine.resource.Resource;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface TableEngineDefinition {

	public void initialize(TableEngine t);

	boolean isFullScreen();

	short getID();

	Resource[] getResourceData();

	Skin getUISkin();

	String getUIStyleName();

	Stage getEntryStage();

	Stage getSettingsStage();

	Network getNetwork();

	Layer[] getLayers();
	
	MenuItem[] getMenuItems();


	public class DefaultTableEngineDefinition implements TableEngineDefinition {

		private TableEngine t;
		private boolean isFullScreen;
		private Network network;
		private Layer[] layers;
		private short id;
		private Resource[] resources;
		private Skin uiSkin;
		private String uiStyleName;
		private Stage entryStage;
		private Stage settingsStage;

		public static final String CONTENT = "CONTENT";
		public static final String FOREGROUND = "FOREGROUND";
		public static final String POPUP = "POPUP";
		public static final String MENU = "MENU";
		
		public DefaultTableEngineDefinition() {

		}

		public DefaultTableEngineDefinition(final Resource[] resources,
				final Network network, final Layer[] layers,
				final boolean isFullscreen, final short id,
				final Skin uiSkin, final String uiStyleName,
				final Stage entryStage, final Stage settingsStage) {

			this.network = network;
			this.layers = layers;
			this.isFullScreen = isFullscreen;
			this.id = id;
			this.resources = resources;
			this.uiSkin = uiSkin;
			this.uiStyleName = uiStyleName;
			this.entryStage = entryStage;
			this.settingsStage = settingsStage;

		}

		@Override
		public boolean isFullScreen() {
			return isFullScreen;
		}

		@Override
		public short getID() {
			return id;
		}

		@Override
		public Resource[] getResourceData() {
			return resources;
		}

		@Override
		public Stage getEntryStage() {
			return entryStage;
		}

		@Override
		public Stage getSettingsStage() {
			return settingsStage;
		}

		@Override
		public Skin getUISkin() {
			return uiSkin;
		}

		@Override
		public String getUIStyleName() {
			return uiStyleName;
		}

		@Override
		public Network getNetwork() {

			return network;
		}

		@Override
		public Layer[] getLayers() {
			return layers;
		}

		public static DefaultTableEngineDefinition getStandardDef() {
		
		return new DefaultTableEngineDefinition(new Resource[]{new BasicResource()}, new TextNetwork(), null, false, (short)
				456, null, null, null, null);
	}

		@Override
		public void initialize(TableEngine t) {

			this.t = t;
		}
		
		public TableEngine t() {
			return t;
		}

		@Override
		public MenuItem[] getMenuItems() {
			
			return null;
		}
	}
}