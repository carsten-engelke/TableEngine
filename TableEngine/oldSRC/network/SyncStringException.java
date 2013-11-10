/*
 * 
 */
package org.engine.network;

// TODO: Auto-generated Javadoc
/**
 * The Class SyncStringException.
 */
public class SyncStringException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The new string. */
	private final String newString;

	/** The original string. */
	private final String originalString;

	/**
	 * Instantiates a new sync string exception.
	 * 
	 * @param originalString
	 *            the original string
	 * @param newString
	 *            the new string
	 */
	public SyncStringException(final String originalString,
			final String newString) {

		this.originalString = originalString;
		this.newString = newString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {

		return "String was changed due to not allowed characters: & and ' are not allowed and will be skipped or replaced by \":\noriginal: "
				+ originalString + "new: " + newString;
	}
}
