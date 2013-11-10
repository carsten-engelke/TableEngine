/*
 * 
 */
package org.engine.model;

import org.engine.Layer;
import org.engine.utils.Array;
import org.engine.Interactable;

public interface Model {

	public Interactable addObject(Interactable obj);

	public Interactable[] addObjects(Interactable[] objs);
	
	public Array<Interactable> getAllObjects();

	public Layer getParentLayer();

	public void removeAllObjects();

	public boolean removeObject(Interactable obj);

	public boolean removeObjects(Interactable[] objs);

	public void initialize(Layer l);

}
