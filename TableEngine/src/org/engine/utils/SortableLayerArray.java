package org.engine.utils;

import java.util.Comparator;

import org.engine.Layer;

import com.badlogic.gdx.utils.Sort;

class DepthHighestAboveLayerComparator implements Comparator<Layer> {

	@Override
	public int compare(final Layer o1, final Layer o2) {

		return o1.depth.get() - o2.depth.get();
	}

	@Override
	public String toString() {
		return "DEPTH: HIGHEST ABOVE";
	}

}

class DepthHighestBelowLayerComparator implements Comparator<Layer> {

	@Override
	public int compare(final Layer o1, final Layer o2) {

		return o2.depth.get() - o1.depth.get();
	}

	@Override
	public String toString() {
		return "DEPTH: HIGHEST BELOW";
	}
}

/**
 * The Class SortedLayerList. Can sort all added Layers with it's sort method.
 * Deploys prepared Comparators for depth comparison.
 */
public class SortableLayerArray extends Array<Layer> {

	public static final Comparator<Layer> COMPARE_DEPTH_HIGHEST_ABOVE = new DepthHighestAboveLayerComparator();

	/** Comparator that sorts Layers according to their depth. */
	public static final Comparator<Layer> COMPARE_DEPTH_HIGHEST_BELOW = new DepthHighestBelowLayerComparator();

	/** The Comparator used to sort the list */
	private final Comparator<Layer> c;

	public SortableLayerArray() {

		super();
		setOrdered(true);
		c = SortableLayerArray.COMPARE_DEPTH_HIGHEST_BELOW;
	}

	public SortableLayerArray(final Comparator<Layer> c) {

		super();
		setOrdered(true);
		this.c = c;
	}

	@Override
	public void add(final Layer value) {

		super.add(value);
		sort();
	}
	
	@Override
	public void addAll(Array<Layer> array) {

		super.addAll(array);
		sort();
	}

	@Override
	public boolean removeValue(final Layer value, final boolean identity) {
		final boolean b = super.removeValue(value, identity);
		sort();
		return b;
	}

	@Override
	public void sort() {
		Sort.instance().sort(a, c);
	}
}