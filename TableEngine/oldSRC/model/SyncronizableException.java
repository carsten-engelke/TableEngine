/*
 * 
 */
package org.engine.model;

import org.engine.Synchronizable;

// TODO: Auto-generated Javadoc
/**
 * The Class SyncronizableException.
 */
public class SyncronizableException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The error. */
	Synchronizable error;

	/** The in. */
	private final String in;

	/** The out. */
	private final String out;

	/**
	 * Instantiates a new syncronizable exception.
	 * 
	 * @param error
	 *            the error
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 */
	public SyncronizableException(final Synchronizable error, final String in,
			final String out) {

		this.in = in;
		this.out = out;
		this.error = error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {

		return "Syncronizable not working correctly: "
				+ error.getClass().getCanonicalName() + " with input:\n" + in
				+ " produced output:\n" + out;
	}
}
