package org.engine.network;

/**
 * The surrveilance class, that is notified when the Engine is set to busy
 * (when starting a drag operation, waiting for input, generally upon user
 * interaction).
 */
public class InterruptionListener {

	private boolean interrupted = false;

	public boolean gotInterrrupted() {

		return interrupted;
	}

	public void setInterrupted(final boolean interrupt) {

		if (interrupt) {
			interrupted = true;
		}
	}
	
	public void resetListener() {
		
		interrupted = false;
	}
}
