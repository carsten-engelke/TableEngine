package org.engine;

import org.engine.gui.GUI;
import org.engine.gui.GUI.GUIProperty;
import org.engine.object.Object;
import org.engine.property.BooleanProperty;
import org.engine.property.Information;
import org.engine.property.IntegerProperty;
import org.engine.property.Property;
import org.engine.property.StringProperty;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;

public class Layer {

	public static final String LAYER_TAG = "L";

	public IntegerProperty depth;

	public StringProperty label;

	public BooleanProperty persistent;

	protected Array<Interactable> a = new Array<Interactable>();

	public GUIProperty gui;

	public TableEngine t;

	public Layer(GUI gui, String label, int depth, boolean persistent) {

		this.gui = new GUIProperty("GUI", LAYER_TAG, gui);
		this.label = new StringProperty("LABEL", LAYER_TAG, label);
		this.depth = new IntegerProperty("DEPTH", LAYER_TAG, depth);
		this.persistent = new BooleanProperty("PERS", LAYER_TAG, persistent);
	}

	public void initialize(TableEngine t) {

		this.t = t;
		gui.get().initialize(this);
	}

	public void addInteractable(Interactable i) {

		a.add(i);
		i.initialize(this);
	}

	public void removeInteractable(Interactable i) {

		a.removeValue(i, true);
	}

	public Array<Interactable> getAllInteractables() {

		return a;
	}

	public static class SyncLayer extends Layer implements Synchronizable {

		public SyncLayer(GUI gui, String label, int depth, boolean persistent) {
			super(gui, label, depth, persistent);
		}

		@Override
		public void setPropertiesFromInformation(Array<Information> a) {

			for (Information info : a) {

				// check whether info belongs to an already existing property of
				// this label.
				boolean isAlreadyInLabel = false;
				Property<?>[] ap = new Property[] { depth, gui, label,
						persistent };
				for (Property<?> p : ap) { // check permanent properties

					if (p.info().id.equals(info.id)) {
						isAlreadyInLabel = true;
						p.applySyncInfo(info);
					}
				}
				for (Interactable i : this.a) { // check objects
					if (Property.class.isAssignableFrom(i.getClass())) {
						Property<?> p = (Property<?>) i;
						if (p.info().id.equals(info.id)) {
							p.applySyncInfo(info);
							isAlreadyInLabel = true;
						}
					}
				}
				if (!isAlreadyInLabel) {

					// the info was not assignable to an property. Try to create
					// an Object from it.
					try {
						// An object's ID is made of it's class' name, a "-" and
						// a running number
						// See TableEngine.getNewObjectID(Class<?> c) for more
						// info.
						String canonicalName = info.id.substring(0,
								info.id.indexOf("-"));
						final Object o = Object.class.cast(Class.forName(
								canonicalName).newInstance());
						o.initialize(info, this);
						o.applySyncInfo(info);
						this.a.add(o);
					} catch (Exception e) {
						Gdx.app.error("PROPERTY_ERROR", "CLASS NOT CREATED: "
								+ info.toString());
					}
				}
			}
		}

		@Override
		public Array<Property<?>> getProperties() {

			Array<Property<?>> propertyArray = new Array<Property<?>>(
					new Property<?>[] { depth, gui, label, persistent });
			for (Interactable i : this.a) {
				if (Property.class.isAssignableFrom(i.getClass())) {
					propertyArray.add(((Property<?>) i));
				}
			}
			return propertyArray;
		}

		@Override
		public Array<Property<?>> getPropertiesFlaggedOnly() {

			Array<Property<?>> propertyArray = new Array<Property<?>>();
			Property<?>[] ap = new Property[] { depth, gui, label, persistent };
			for (Property<?> p : ap) {

				if (p.isFlagged()) {

					propertyArray.add(p);
				}
			}
			for (Interactable i : this.a) {
				if (Property.class.isAssignableFrom(i.getClass())) {

					Property<?> sp = (Property<?>) i;
					if (sp.isFlagged()) {
						propertyArray.add(sp);
					}
				}
			}
			return propertyArray;
		}
	}

	public void clear() {

		a.clear();
	}
}
