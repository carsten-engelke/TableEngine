package org.engine.model;

import org.engine.Layer;
import org.engine.Interactable;
import org.engine.utils.Array;
import org.engine.utils.SortableInteractableArray;

/**
 * The Class PlainModel. A simple class loading and organizing model.
 */
public class PlainModel implements Model {

	private final Array<Interactable> objectList = new Array<Interactable>();

	private Layer l;

	@Override
	public Interactable addObject(final Interactable sync) {

		objectList.add(sync);
		sync.initialize(getParentLayer());
		return sync;
	}
	
	@Override
	public Interactable[] addObjects(final Interactable[] syncs) {

		for (final Interactable sync : syncs) {

			objectList.add(sync);
			sync.initialize(getParentLayer());
		}
		return syncs;
	}

	@Override
	public Array<Interactable> getAllObjects() {

		final SortableInteractableArray depthList = new SortableInteractableArray();
		for (int i = 0; i < objectList.getSize(); i++) {
			final Interactable sync = objectList.get(i);
			depthList.add(sync);
		}
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
		return depthList;
	}
	
	@Override
	public Layer getParentLayer() {

		return l;
	}
	
	@Override
	public void removeAllObjects() {

		objectList.clear();

	}

	@Override
	public boolean removeObject(final Interactable sync) {

		final boolean isInList = objectList.removeValue(sync, true);
		return isInList;
	}

	@Override
	public boolean removeObjects(final Interactable[] syncs) {

		boolean foundAll = true;
		for (final Interactable sync : syncs) {
			foundAll = objectList.removeValue(sync, true);
		}
		return foundAll;
	}

	@Override
	public void initialize(Layer l) {

		this.l = l;
	}

}
