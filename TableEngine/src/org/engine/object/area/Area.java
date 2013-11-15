/*
 * 
 */
package org.engine.object.area;

import org.engine.Interactable;
import org.engine.geometry.Rectangle;

/**
 * The Interface Area.
 */
public interface Area extends Interactable {

	/**
	 * Receive action request. Usually called when an object is dragged &
	 * dropped,
	 * 
	 * @param sender
	 *            the sender
	 * @param rectangle
	 *            the sender's bounds
	 * @return true, if overlapping with this area.
	 */
	public boolean receiveActionRequest(Interactable sender,
			Rectangle rectangle);

}
