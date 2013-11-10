package org.engine.magnifier;

import org.engine.Synchronizable;

public interface Magnifier extends Synchronizable {

	public Object getFocussedObject();

	public float isMagnificating();

	public void setFocussedObject(String id);

	public void setMagnificating(float multiplier);
}
