package org.engine;

import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

public interface Synchronizable {

	public void setPropertiesFromInformation(Array<Information> p);

	public Array<Property<?>> getProperties();

	public Array<Property<?>> getPropertiesFlaggedOnly();
}
