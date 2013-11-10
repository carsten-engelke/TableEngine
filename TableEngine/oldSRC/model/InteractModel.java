package org.engine.model;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.Synchronizable.Property;
import org.engine.network.Network;
import org.engine.utils.Array;
import org.engine.utils.SortableInteractableArray;

public class InteractModel implements Model {

	private final Array<Interactable> objectList = new Array<Interactable>();

	private Layer l;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#addFirstObject(java.lang.String)
	 */
	@Override
	public Synchronizable addFirstObject(final String syncString) {

		Synchronizable[] s;
		try {
			s = getInstancesFromString(syncString);
			if (s.length > 0) {
				objectList.add(s[0]);
				return s[0];
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#addObject(org.engine.Synchronizable)
	 */
	@Override
	public Synchronizable addObject(final Synchronizable sync) {

		objectList.add(sync);
		sync.initialize(getParentLayer());
		return sync;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#addObjects(java.lang.String)
	 */
	@Override
	public Synchronizable[] addObjects(final String contentString) {

		Synchronizable[] s;
		try {
			s = getInstancesFromString(contentString);
			for (final Synchronizable sync : s) {

				objectList.add(sync);

			}
			return s;

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#addObjects(org.engine.Synchronizable[])
	 */
	@Override
	public Synchronizable[] addObjects(final Synchronizable[] syncs) {

		for (final Synchronizable sync : syncs) {

			objectList.add(sync);
			sync.initialize(getParentLayer());
		}
		return syncs;
	}

	@Override
	public String createLayerString() {

		final Array<Synchronizable> syncs = getAllObjects();
		String s = "";
		for (final Synchronizable sync : syncs) {

			s += getStringFromInstance(sync);
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#getAllObjects()
	 */
	@Override
	public Array<Synchronizable> getAllObjects() {

		final SortableInteractableArray depthList = new SortableInteractableArray();
		for (int i = 0; i < objectList.getSize(); i++) {
			final Synchronizable sync = objectList.get(i);
			depthList.add(sync);
		}
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
		return depthList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#getFirstObject(java.lang.String)
	 */
	@Override
	public Synchronizable getFirstObject(final String syncString) {

		for (int i = 0; i < objectList.getSize(); i++) {

			if (objectList.get(i).toString().equals(syncString)) {

				return objectList.get(i);

			}
		}
		return null;
	}

	/**
	 * Gets the instances from a String.
	 * 
	 * @param contentString
	 *            the String describing the Synchronizables (gained through
	 *            Model.toString e.g.). -> MUST BE IN CORRECT NETWORK FORMAT!!!
	 *            <obj=full.classified.name>information-string</obj>
	 * @return the instances from string
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws SyncronizableException
	 *             the syncronizable exception
	 */
	private Synchronizable[] getInstancesFromString(final String contentString)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		if (contentString.contains(Network.INSTANCE_END)) {
			final String[] syncStrings = contentString
					.split(Network.INSTANCE_END);
			final Synchronizable[] syncs = new Synchronizable[syncStrings.length];
			for (int j = 0; j < syncs.length; j++) {

				final String ClassPathDefinition = syncStrings[j].substring(
						syncStrings[j].indexOf(Network.INSTANCE_BEGIN)
								+ Network.INSTANCE_BEGIN.length(),
						syncStrings[j].indexOf(Network.INSTANCE_MIDDLE));
				final String ClassInstanceDefinition = syncStrings[j]
						.substring(syncStrings[j]
								.indexOf(Network.INSTANCE_MIDDLE)
								+ Network.INSTANCE_MIDDLE.length());

				@SuppressWarnings("unchecked")
				final Class<Synchronizable> c = (Class<Synchronizable>) Synchronizable.class
						.getClassLoader().loadClass(ClassPathDefinition);
				final Synchronizable sync = c.newInstance();
				final Array<Property> defaultProps = sync.getProperties();
				// set default values to received values
				for (final String realProp : ClassInstanceDefinition
						.split(Synchronizable.DELIMITER)) {
					final String[] prop = realProp
							.split(Synchronizable.SEPARATOR);
					final String id = prop[0];
					defaultProps.get(id).content = prop[1];
				}
				sync.setProperties(defaultProps);
				sync.initialize(getParentLayer());
				syncs[j] = sync;
			}

			return syncs;
		} else {
			return new Synchronizable[0];
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#getParentLayer()
	 */
	@Override
	public Layer getParentLayer() {

		return l;
	}

	/**
	 * Gets the string from an instance of Synchronizable. Turns it into the
	 * following format: <br>
	 * <obj=full.classified.name>information-string</obj>
	 * 
	 * @param sync
	 *            the Synchronizable
	 * @return the string from instance
	 */
	private String getStringFromInstance(final Synchronizable sync) {

		String s = Network.INSTANCE_BEGIN + sync.getClass().getCanonicalName()
				+ Network.INSTANCE_MIDDLE;
		for (final Property p : sync.getProperties()) {

			s += p.propToString() + Synchronizable.DELIMITER;

		}
		s = s.substring(0, s.length() - 1);
		s += Network.INSTANCE_END;

		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#removeAllObjects()
	 */
	@Override
	public void removeAllObjects() {

		objectList.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#removeFirstObject(java.lang.String)
	 */
	@Override
	public boolean removeFirstObject(final String syncString) {

		for (int i = 0; i < objectList.getSize(); i++) {

			if (objectList.get(i).toString().equals(syncString)) {

				objectList.removeIndex(i);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#removeObject(org.engine.Synchronizable)
	 */
	@Override
	public boolean removeObject(final Synchronizable sync) {

		final boolean isInList = objectList.removeValue(sync, true);
		return isInList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.model.Model#removeObjects(org.engine.Synchronizable[])
	 */
	@Override
	public boolean removeObjects(final Synchronizable[] syncs) {

		boolean foundAll = true;
		for (final Synchronizable sync : syncs) {
			foundAll = objectList.removeValue(sync, true);
		}
		return foundAll;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Model#setParentLayer(org.engine.Layer)
	 */
	@Override
	public void setParentLayer(final Layer l) {

		this.l = l;
	}

}
