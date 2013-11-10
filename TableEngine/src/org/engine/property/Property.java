package org.engine.property;

public interface Property<T> {

	public Information info();

	public void setFlagged(boolean flagged);

	public boolean isFlagged();

	public void applySyncInfo(Information i);

	public T get();

	public void set(T value);
}
