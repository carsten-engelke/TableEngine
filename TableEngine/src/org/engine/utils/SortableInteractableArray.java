/*
 * 
 */
package org.engine.utils;

import java.util.Comparator;

import org.engine.Interactable;

import com.badlogic.gdx.utils.Sort;

// TODO: Auto-generated Javadoc
/**
 * The Class SortedSyncList.
 */
public class SortableInteractableArray extends Array<Interactable> {

//	/** The Constant COMPARE_X. */
//	public static final Comparator<Interactable> COMPARE_X = new XSyncComparator();

	public static final Comparator<Interactable> COMPARE_Z_HIGHEST_ABOVE = new ZSyncHighestAboveComparator();

	/** The Constant COMPARE_DEPTH. */
	public static final Comparator<Interactable> COMPARE_Z_HIGHEST_BELOW = new ZSyncHighestBelowComparator();

	/**
	 * Instantiates a new sorted sync classList.
	 * 
	 * @param c
	 *            the c
	 */

	public SortableInteractableArray() {

		super();
		setOrdered(true);
	}

	@Override
	public void sort(final Comparator<Interactable> c) {

		Sort.instance().sort(a, c);
	}
}

//class XSyncComparator implements Comparator<Interactable> {
//
//	@Override
//	public int compare(final Interactable o1, final Interactable o2) {
//
//		return (int) (o2.getPosition().z - o1.getPosition().z);
//
//	}
//
//}

class ZSyncHighestAboveComparator implements Comparator<Interactable> {

	@Override
	public int compare(final Interactable o1, final Interactable o2) {

		return (int) (o1.depth() - o2.depth());
	}

	@Override
	public String toString() {
		return "COMPARATOR_INTERACTABLE - HIGHEST ABOVE";
	}
}

class ZSyncHighestBelowComparator implements Comparator<Interactable> {

	@Override
	public int compare(final Interactable o1, final Interactable o2) {

		return (int) (o2.depth() - o1.depth());
	}

	@Override
	public String toString() {
		return "COMPARATOR_INTERACTABLE - HIGHEST BELOW";
	}
}
