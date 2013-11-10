/*
 * 
 */
package org.engine.object.area;

import org.engine.Synchronizable;

import org.engine.geometry.Rectangle;

// TODO: Auto-generated Javadoc
/**
 * The Interface Area.
 */
public interface Area extends Synchronizable {

	/**
	 * Receive action request. Usually called when an object is dragged &
	 * dropped,
	 * 
	 * @param sender
	 *            the sender
	 * @param rectangle
	 *            the sender area
	 * @return true, if successful
	 */
	public boolean receiveActionRequest(Synchronizable sender,
			Rectangle rectangle);

}
