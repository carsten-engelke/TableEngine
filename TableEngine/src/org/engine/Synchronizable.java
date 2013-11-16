package org.engine;

import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

/**
 * A synchronizable is defined by several {@link Property Properties}. Hence a
 * <code>Property</code> is an Object or Primitive that is turnable into a
 * String, the definition of an <code>Synchronizable</code> is easily
 * transportable by aquiring all it's <code>Properties</code> and turning them
 * into a String.
 * 
 * The Synchronizable can apply this String information to it's properties and
 * viceversa.
 */
public interface Synchronizable {

	/**
	 * Sets the properties from an {@link Array} of {@link Information} objects.
	 * These are aplicable to the {@link Synchronizable Synchronizable's}
	 * {@link Property Properties}.
	 * 
	 * This method is usually called when TableEngine received data from server.
	 * 
	 * @param a
	 *            the <code>Information</code> objects to apply.
	 */
	public void setPropertiesFromInformation(Array<Information> a);

	/**
	 * Gets the {@link Property Properties}.
	 * 
	 * @return the properties that define this <code>Synchronizable</code>
	 */
	public Array<Property<?>> getProperties();

	/**
	 * Gets the flagged {@link Property Properties}.
	 * 
	 * @return the <code>Properties</code> that are flagged. Should return
	 *         <code>null</code> if no <code>Property</code> is flagged.
	 */
	public Array<Property<?>> getPropertiesFlagged();
}
