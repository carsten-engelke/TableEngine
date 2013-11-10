package org.engine.utils;

import java.util.Comparator;

import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Sort;

public class ArrayMap<K, V> {
	
	class KeyComparator implements Comparator<Entry<K, V>> {

		public Comparator<K> c;

		public KeyComparator() {
			
			c = null;
		}
		
		public KeyComparator(final Comparator<K> c) {

			this.c = c;
		}

		@Override
		public int compare(final Entry<K, V> o1, final Entry<K, V> o2) {

			if (c != null) {
				
				return c.compare(o1.key, o2.key);
			} else {
				
			    return o1.key.toString().compareTo(o2.key.toString());
			}
		}

	}

	public interface NotificationListener<K, V> {

		public void arrayMapChanged(ArrayMap<K, V> newArrayMap);

	}

	class ValueComparator implements Comparator<Entry<K, V>> {

		public Comparator<V> c;

		public ValueComparator() {
			
			c = null;
		}
		public ValueComparator(final Comparator<V> c) {

			this.c = c;
		}

		@Override
		public int compare(final Entry<K, V> o1, final Entry<K, V> o2) {

			if (c != null) {
				
				return c.compare(o1.value, o2.value);
			} else {
				
				return o1.value.toString().compareTo(o2.value.toString());
			}
			
		}

	}

	private NotificationListener<K, V> l = new NotificationListener<K, V>() {

		@Override
		public void arrayMapChanged(final ArrayMap<K, V> newArrayMap) {

			// OVERRIDE ME
		}
	};

	public com.badlogic.gdx.utils.ArrayMap<K, V> m;
	
	public ArrayMap() {

		m = new com.badlogic.gdx.utils.ArrayMap<K, V>();
	}

	public ArrayMap(final ArrayMap<K, V> m) {

		this.m = new com.badlogic.gdx.utils.ArrayMap<K, V>(m.m);
	}

	public ArrayMap(final boolean ordered, final int capacity) {

		this.m = new com.badlogic.gdx.utils.ArrayMap<K, V>(ordered, capacity);
	}

	public ArrayMap(final boolean ordered, final int capacity,
			final Class<K> keyArrayType, final Class<V> valueArrayType) {

		this.m = new com.badlogic.gdx.utils.ArrayMap<K, V>(ordered, capacity,
				keyArrayType, valueArrayType);
	}

	public ArrayMap(final Class<K> keyArrayType, final Class<V> valueArrayType) {

		this.m = new com.badlogic.gdx.utils.ArrayMap<K, V>(keyArrayType,
				valueArrayType);
	}

	public ArrayMap(final int capacity) {

		this.m = new com.badlogic.gdx.utils.ArrayMap<K, V>(capacity);
	}

	public void addAll(final ArrayMap<K, V> map) {

		m.putAll(map.m);
		l.arrayMapChanged(this);
	}

	public void addAll(final ArrayMap<K, V> map, final int offset,
			final int length) {

		m.putAll(map.m, offset, length);
		l.arrayMapChanged(this);
	}

	public void clear() {

		m.clear();
		l.arrayMapChanged(this);
	}

	public boolean containsKey(final K key) {

		return m.containsKey(key);
	}

	public boolean containsValue(final V value, final boolean identity) {

		return m.containsValue(value, identity);
	}

	public Iterable<Entry<K, V>> entries() {

		return m.entries();
	}

	public K getKeyAt(final int index) {

		return m.getKeyAt(index);
	}

	public Iterable<K> getKeys(final V value, final boolean identity) {

		final Array<K> k = new Array<K>();
		for (final Entry<K, V> e : m.entries()) {

			if (identity) {
				if (e.value == value) {
					k.add(e.key);
				}
			} else {
				if (e.value.equals(value)) {
					k.add(e.key);
				}
			}
		}
		return k;
	}

	public NotificationListener<K, V> getNotificationListener() {
		return l;
	}

	public V getValue(final K key) {

		return m.get(key);
	}

	public V getValueAt(final int index) {

		return m.getValueAt(index);
	}

	public Iterable<V> getValues(final String compareToString) {

		final Array<V> values = new Array<V>(1);
		for (int i = 0; i < m.size; i++) {
			if (m.keys[i].toString().equals(compareToString)) {
				values.add(m.values[i]);
			}
		}
		return values;
	}

	public Iterable<K> keys() {

		return m.keys();
	}

	public void put(final K key, final V value) {

		m.put(key, value);
		l.arrayMapChanged(this);
	}

	public void put(final K key, final V value, final int index) {

		m.put(key, value, index);
		l.arrayMapChanged(this);
	}

	public void removeIndex(final int index) {

		m.removeIndex(index);
		l.arrayMapChanged(this);
	}

	public void removeKey(final K key) {

		m.removeKey(key);
		l.arrayMapChanged(this);
	}

	public void removeValue(final V value, final boolean identity) {

		m.removeValue(value, identity);
		l.arrayMapChanged(this);
	}

	public void setNotificationListener(final NotificationListener<K, V> l) {
		this.l = l;
	}

	public void shrink() {

		m.shrink();
	}

	public void shuffle() {

		m.shuffle();
		l.arrayMapChanged(this);
	}

	public void sortToKeys() {

		sortToKeys(new KeyComparator().c);
	}
	
	public void sortToKeys(final Comparator<K> c) {

		final KeyComparator kc = new KeyComparator(c);
		final Array<Entry<K, V>> a = createEntryArray();
		Sort.instance().sort(a.a, kc);
		m.clear();
		for (final Entry<K, V> e : a) {
			m.put(e.key, e.value);
		}
		l.arrayMapChanged(this);
	}

	private Array<Entry<K, V>> createEntryArray() {

		Array<Entry<K, V>> a = new Array<Entry<K,V>>();
		for (Entry<K, V> e : m.entries()) {
			Entry<K, V> addMe = new Entry<K, V>();
			addMe.key = e.key;
			addMe.value = e.value;
			a.add(addMe);
		}
		return a;
	}

	public void sortToValues() {

		sortToValues(new ValueComparator().c);
	}

	public void sortToValues(final Comparator<V> c) {

		final ValueComparator vc = new ValueComparator(c);
		final Array<Entry<K, V>> a = createEntryArray();
		Sort.instance().sort(a.a, vc);
		m.clear();
		for (final Entry<K, V> e : a) {
			m.put(e.key, e.value);
		}
		l.arrayMapChanged(this);
	}

	public Array<Entry<K, V>> toArray() {

		return new Array<Entry<K, V>>(entries());
	}

	@Override
	public String toString() {

		String s = Array.ENUM_START;
		for (int i = 0; i < m.size; i++) {
			s += m.keys[i].toString() + "=" + m.values[i].toString()
					+ Array.ENUM_DELIMITER;
		}
		if (s.contains(Array.ENUM_DELIMITER)) {
			s = s.substring(0,
					s.length() - Array.ENUM_DELIMITER.length());
		}
		s += Array.ENUM_END;
		return s;
	}

	public String toString(final String start, final String arraySeparator, final String separator, final String end) {

		String s = start;
		for (int i = 0; i < m.size; i++) {
			s += m.keys[i].toString() + separator + m.values[i].toString()
					+ arraySeparator;
		}
		if (s.contains(arraySeparator)) {
			s = s.substring(0, s.length() - arraySeparator.length());
		}
		s += end;
		return s;
	}

	public Iterable<V> values() {

		return m.values();
	}

	public int getSize() {

		return m.size;
	}
}
