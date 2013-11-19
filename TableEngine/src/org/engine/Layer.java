package org.engine;

import org.engine.gui.GUI;
import org.engine.gui.GUIProperty;
import org.engine.object.Object;
import org.engine.property.BooleanProperty;
import org.engine.property.Information;
import org.engine.property.IntegerProperty;
import org.engine.property.Property;
import org.engine.property.Property.Flag;
import org.engine.property.StringProperty;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;

/**
 * The Class Layer contains {@link Interactable} objects (@link #a} and a
 * {@link GUI} for IO access to these objects. See
 * {@link TableEngine#input(org.engine.gui.input.InputEvent)} and
 * {@link TableEngine#output(org.engine.gui.output.Graphics)} for more info.
 */
public class Layer {

	/** The Constant LAYER_TAG is to be used by all properties of a Label */
	public static final String LAYER_TAG = "L";

	// RIGID PROPERTIES
	/** The depth. */
	public IntegerProperty depth;

	/**
	 * The label. {@link TableEngine#getLayer(String)} returns this layer when
	 * fed with it's label.
	 */
	public StringProperty label;

	/**
	 * If persistent, TableEngine will not forward input to underlying labels if
	 * one of the Interactables of this label returns true when confronted with
	 * an InputEvent
	 */
	public BooleanProperty persistent;

	/**
	 * The gui. Used by TableEngine to access input and output of the
	 * Interactables.
	 */
	public GUIProperty gui;

	/** The rigid properties depth, label, persistent and gui. */
	protected Property<?>[] rigidProps;

	/** The property array. */
	protected Array<Property<?>> propertyArray;

	//
	/** The {@link Interactable} array stores added Interactables. */
	protected Array<Interactable> a = new Array<Interactable>();

	/** The parent TableEngine */
	public TableEngine t;

	protected Property<?> tmpProp;

	/**
	 * Instantiates a new layer.
	 * 
	 * @param gui
	 *            {@link #gui}
	 * @param label
	 *            {@link #label}
	 * @param depth
	 *            {@link #depth}
	 * @param persistent
	 *            {@link #persistent}
	 */
	public Layer(GUI gui, String label, int depth, boolean persistent) {

		this.gui = new GUIProperty("GUI", LAYER_TAG, Flag.NONE, gui);
		this.label = new StringProperty("LABEL", LAYER_TAG, Flag.NONE, label);
		this.depth = new IntegerProperty("DEPTH", LAYER_TAG, Flag.NONE, depth);
		this.persistent = new BooleanProperty("PERS", LAYER_TAG, Flag.NONE,
				persistent);
		rigidProps = new Property[] { this.depth, this.gui, this.label,
				this.persistent };
		propertyArray = new Array<Property<?>>(rigidProps);
	}

	/**
	 * Initialize this label. Called automatically by TableEngine.
	 * 
	 * @param t
	 *            the t
	 */
	public void initialize(TableEngine t) {

		this.t = t;
		gui.get().initialize(this);
	}

	/**
	 * Adds an {@link Interactable}. Automatically initializes it.
	 * 
	 * @param i
	 *            the <code>Interactable</code> to add.
	 */
	public void addInteractable(Interactable i) {

		a.add(i);
		i.initialize(this);
	}

	/**
	 * Removes an {@link Interactable}.
	 * 
	 * @param i
	 *            the <code>Interactable</code> to remove.
	 */
	public void removeInteractable(Interactable i) {

		a.removeValue(i, true);
	}

	/**
	 * Gets all {@link Interactable} objects.
	 * 
	 * @return all interactables
	 */
	public Array<Interactable> getAllInteractables() {

		return a;
	}

	/**
	 * The Class SyncLayer.
	 */
	public static class SyncLayer extends Layer implements Synchronizable {

		/**
		 * Instantiates a new sync layer.
		 * 
		 * @param gui
		 *            the gui
		 * @param label
		 *            the label
		 * @param depth
		 *            the depth
		 * @param persistent
		 *            the persistent
		 */
		public SyncLayer(GUI gui, String label, int depth, boolean persistent) {
			super(gui, label, depth, persistent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.engine.Synchronizable#setPropertiesFromInformation(org.engine
		 * .utils.Array)
		 */
		@Override
		public void setPropertiesFromInformation(Array<Information> a) {

			for (Information info : a) {

				// check whether info belongs to an already existing property of
				// this label.
				boolean isAlreadyInLabel = false;
				for (Property<?> p : rigidProps) { // check permanent properties

					if (p.info().id.equals(info.id)) {
						isAlreadyInLabel = true;
						p.applySyncInfo(info);
					}
				}
				for (Interactable i : this.a) { // check objects
					if (Property.class.isAssignableFrom(i.getClass())) {
						tmpProp = (Property<?>) i;
						if (tmpProp.info().id.equals(info.id)) {
							tmpProp.applySyncInfo(info);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.engine.Synchronizable#getProperties()
		 */
		@Override
		public Array<Property<?>> getProperties() {

			propertyArray.clear();
			propertyArray.addAll(rigidProps);
			for (Interactable i : a) {

				if (Property.class.isAssignableFrom(i.getClass())) {
					propertyArray.add(((Property<?>) i));
				} else {
					System.out.println("NOT A PROPERTY:"
							+ i.getClass().getCanonicalName());
				}
			}
			return propertyArray;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.engine.Synchronizable#getPropertiesFlaggedOnly()
		 */
		@Override
		public Array<Property<?>> getPropertiesFlagged() {

			propertyArray.clear();
			for (Property<?> p : rigidProps) {

				if (p.flag() != Flag.NONE) {

					propertyArray.add(p);
				}
			}
			for (Interactable i : this.a) {
				if (Property.class.isAssignableFrom(i.getClass())) {

					tmpProp = (Property<?>) i;
					if (tmpProp.flag() != Property.Flag.NONE) {
						propertyArray.add(tmpProp);
					}
				}
			}
			if (propertyArray.getSize() > 0) {
				return propertyArray;
			}
			return null;
		}
	}

	/**
	 * Clear.
	 */
	public void clear() {

		a.clear();
	}
}
