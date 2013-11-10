/*
 * 
 */
package org.engine.object.popup;

// TODO: Auto-generated Javadoc
/**
 * The Interface PopupSlave.
 */
public interface PopupSlave {

	public static final String CANCEL_ORDER = "CANCEL";

	/**
	 * Receive order.
	 * 
	 * @param order
	 *            the order
	 */
	public void receiveOrder(String order);

}
