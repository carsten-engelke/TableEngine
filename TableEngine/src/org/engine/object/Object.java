package org.engine.object;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.property.Information;
import org.engine.property.Property;

public interface Object extends Property<Synchronizable>, Interactable {

	public void initialize(Information i, Layer l);
}
