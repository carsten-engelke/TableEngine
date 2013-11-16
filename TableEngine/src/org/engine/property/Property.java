package org.engine.property;

import org.engine.Synchronizable;
import org.engine.TableEngine;

/**
 * The Interface <code>Property</code> is wrapped around a Class or Primitive
 * (as generic type).
 * 
 * <code>Properties</code> are the objects that define the instance of a
 * {@link Synchronizable}.
 * 
 * Implementing classes have to turn this type into a String and vice-versa.
 * This way it is easily exchanged via network.The String is provided as an
 * {@link Information} object. This way nested <code>Properties</code> are
 * possible, as well as putting a {@link Flag} on the <code>Property</code>.
 * Each <code>Property</code> should have a unique ID, and a TAG that identifies
 * it's parent <code>Synchronizable's</code> type or level of nesting.
 * 
 * There are working <code>Properties</code> for the Primitives like
 * {@link IntegerProperty}, {@link StringProperty} etc. and for nesting
 * {@link SyncProperty}.
 * 
 * The {@link TableEngine TableEngine's} <code>Properties</code> are called by
 * the Network automatically. See {@link TableEngine#requestSend()} and
 * {@link TableEngine#requestReceive()} for more information.
 * 
 * @param <T>
 *            the generic type
 */
public interface Property<T> {

	/**
	 * Gets the current {@link Information} object that represents this
	 * Property. This object is turned to a String by the Network automatically.
	 * 
	 * @return the <code>Information</code> object
	 */
	public Information info();

	/**
	 * Sets a {@link Flag} to this {@link Property}. This is later seen by the
	 * Network, send to server and reseted. Update it before handing out.
	 * 
	 * @param f
	 *            the flag
	 */
	public void setFlag(Flag f);

	/**
	 * Returns whether this {@link Property} was flagged or not.
	 * 
	 * @return the flag
	 */
	public Flag flag();

	/**
	 * Changes the generic type according to the {@link Information's} String.
	 * 
	 * @param i
	 *            the i
	 */
	public void applySyncInfo(Information i);

	/**
	 * Gets the actual object this {@link Property} was wrapped around.
	 * 
	 * @return the Object or Primitive.
	 */
	public T get();

	/**
	 * Sets the actual object to the value.
	 * 
	 * @param value
	 *            is the new value.
	 */
	public void set(T value);

	/**
	 * Possible values for the {@link Property#setFlag(Flag)} and
	 * {@link Property#flag()} methods.
	 * 
	 */
	public enum Flag {

		/** Not flagged. */
		NONE, /** Add or update this Property. */
		ADD_CHANGE, /** Remove this Property. */
		REMOVE;
	}
}
