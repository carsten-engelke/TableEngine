/*
 * 
 */
package org.engine.network;

import org.engine.Universe;

/**
 * The Class ReturnNetworkThread. 
 */
public class ReturnNetworkThread extends Thread {

	/** The millis. */
	private final long millis;

	/** The still change. */
	boolean stillChange = true;

	/** The uni. */
	private final Universe uni;

	/**
	 * Instantiates a new return network thread.
	 * 
	 * @param uni
	 *            the uni
	 * @param millis
	 *            the millis
	 */
	public ReturnNetworkThread(final Universe uni, final long millis) {

		this.uni = uni;
		this.millis = millis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		uni.addBusyListener(new InterruptionListener() {

			@Override
			public void setInterrupted(final boolean newValue) {
				super.setInterrupted(newValue);
				stillChange = false;
			}
		});
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		if (stillChange) {
			uni.setBusy(false);
		}
	}
}
