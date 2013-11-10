package org.engine.utils;

import java.util.Comparator;
import java.util.Iterator;

public class Array<T> implements Iterable<T> {

	public interface NotificationListener<T> {

		public void arrayChanged(Array<T> newArray);
	}

	public static final String ENUM_START = "[";

	public static final String ENUM_DELIMITER = ",";

	public static final String ENUM_END = "]";

	public static final String ARRAY_TAG = "A";

	com.badlogic.gdx.utils.Array<T> a;

	NotificationListener<T> l = new NotificationListener<T>() {

		@Override
		public void arrayChanged(final Array<T> newArray) {

			// OVERRIDE ME
		}
	};

	private int limit = -1;

	public Array() {
		a = new com.badlogic.gdx.utils.Array<T>();
	}

	public Array(final Array<T> a) {
		this.a = new com.badlogic.gdx.utils.Array<T>(a.a);
	}

	public Array(final boolean ordered, final int capacity) {
		a = new com.badlogic.gdx.utils.Array<T>(ordered, capacity);
	}

	public Array(final boolean ordered, final int capacity,
			final Class<T> arrayType) {
		a = new com.badlogic.gdx.utils.Array<T>(ordered, capacity, arrayType);
	}

	public Array(final boolean ordered, final T[] array, int start, int count) {
		a = new com.badlogic.gdx.utils.Array<T>(ordered, array, start, count);
	}

	public Array(final Class<T> arrayType) {
		a = new com.badlogic.gdx.utils.Array<T>(arrayType);
	}

	public Array(final int capacity) {
		a = new com.badlogic.gdx.utils.Array<T>(capacity);
	}
	
	public Array(final int capacity, final int limit) {
		a = new com.badlogic.gdx.utils.Array<T>(capacity);
		this.limit = limit;
	}

	public Array(final Iterable<T> i) {

		a = new com.badlogic.gdx.utils.Array<T>();
		for (final T t : i) {
			a.add(t);
		}
	}

	public Array(final T[] array) {
		a = new com.badlogic.gdx.utils.Array<T>(array);
	}

	public void add(final T value) {

		a.add(value);
		reduceToLimit();
		l.arrayChanged(this);

	}

	public void addAll(final Array<T> array) {

		a.addAll(array.a);
		reduceToLimit();
		l.arrayChanged(this);

	}
	
	public void addAll(Iterable<T> array) {
		
		for (T value : array) {
			a.add(value);
		}
		reduceToLimit();
		l.arrayChanged(this);
	}

	public void addAll(final Array<T> array, final int offset, final int length) {

		a.addAll(array.a, offset, length);
		reduceToLimit();
		l.arrayChanged(this);

	}

	public void addAll(final T[] array) {

		a.addAll(array);
		reduceToLimit();
		l.arrayChanged(this);

	}

	public void addAll(final T[] array, final int offset, final int length) {

		a.addAll(array, offset, length);
		reduceToLimit();
		l.arrayChanged(this);

	}

	public void setLimit(int limit) {

		this.limit = limit;
		reduceToLimit();
		a.shrink();
	}

	public int getLimit() {

		return limit;
	}

	private void reduceToLimit() {

		if (limit >= 0) {
			int reduce = a.size - limit;
			for (int i = 0; i < reduce; i++) {
				removeIndex(0);
			}
		}
	}

	public void clear() {

		a.clear();
		l.arrayChanged(this);

	}

	public boolean contains(final T value, final boolean identity) {

		return a.contains(value, identity);
	}

	public T first() {

		return a.first();
	}

	public T get(final int index) {

		return a.get(index);
	}

	// public T get(final String compareToString) {
	//
	// for (int i = 0; i < a.size; i++) {
	// if (a.items[i].toString().equals(compareToString)) {
	// return a.items[i];
	// }
	// }
	// return null;
	// }
	//
	public NotificationListener<T> getNotificationListener() {

		return l;
	}

	public int getSize() {

		return a.size;
	}

	public int indexOf(final T value, final boolean identity) {

		return a.indexOf(value, identity);
	}

	public boolean isOrdered() {

		return a.ordered;
	}

	@Override
	public Iterator<T> iterator() {

		return a.iterator();
	}

	public T last() {

		return a.items[a.size - 1];
	}

	public T popFirst() {
		if (a.size > 0) {
			final T item = a.get(0);
			a.removeIndex(0);
			l.arrayChanged(this);
			return item;
		}
		return null;
	}

	public T popFirstWaiting() {
		T pop = popFirst();
		while (pop == null) {
			pop = popFirst();
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return pop;
	}

	public T popLast() {
		if (a.size > 0) {
			final T item = a.get(a.size - 1);
			a.removeIndex(a.size - 1);
			l.arrayChanged(this);
			return item;
		}
		return null;
	}

	public T popLastWaiting() {
		T pop = popLast();
		while (pop == null) {
			pop = popLast();
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return pop;
	}

	public boolean removeAll(final Array<T> array, final boolean identity) {

		if (a.removeAll(array.a, identity)) {
			l.arrayChanged(this);
			return true;
		}
		return false;
	}

	public T removeIndex(final int index) {

		final T t = a.removeIndex(index);
		l.arrayChanged(this);
		return t;
	}

	public boolean removeValue(final T value, final boolean identity) {

		if (a.removeValue(value, identity)) {
			l.arrayChanged(this);
			return true;
		}
		return false;
	}

	public void set(final int index, final T value) {

		a.set(index, value);
		l.arrayChanged(this);
	}

	public void setNotificationListener(final NotificationListener<T> l) {

		this.l = l;
	}

	public void setOrdered(final boolean ordered) {

		a.ordered = ordered;
	}

	public void shrink() {

		a.shrink();
	}

	public void shuffle() {

		a.shuffle();
		l.arrayChanged(this);
	}

	public void sort() {

		a.sort();
		l.arrayChanged(this);
	}

	public void sort(final Comparator<T> comparator) {

		a.sort(comparator);
		l.arrayChanged(this);
	}

	public T[] toArray() {

		return a.toArray();
	}

	@Override
	public String toString() {

		String s = ENUM_START;
		for (int i = 0; i < a.size; i++) {
			s += a.items[i].toString() + ENUM_DELIMITER;
		}
		if (s.contains(ENUM_DELIMITER)) {
			s = s.substring(0, s.length() - ENUM_DELIMITER.length());
		}
		s += ENUM_END;
		return s;
	}

	public String toString(final String start, final String separator,
			final String end) {

		String s = start;
		for (int i = 0; i < a.size; i++) {
			s += a.items[i].toString() + separator;
		}
		if (s.contains(separator)) {
			s = s.substring(0, s.length() - separator.length());
		}
		s += end;
		return s;
	}
}
