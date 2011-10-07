/* FilterMap.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 25 18:26:47 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util;

import java.util.Iterator;
import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;
import java.util.AbstractSet;

import org.zkoss.lang.Objects;

/**
 * A map that allows the value to be evaluated before returning (so called
 * filtering).
 * For example, if you allow the value to have EL expressions and want to
 * evaluate it before returning, then
 * you could implemnent {@link Filter} to evaluate the value when the value
 * is retrieved. Then, encapsulate the original map with this class. For example,
 * <pre><code>
 return new FilterMap(map,
     new FilterMap.Filter() {
         public Object filter(Object key, Object value) {
             //evaluate the value and return the result
         }
     });
</code></pre>
 *
 * <p>Notice that this map is readonly, and it is thread-safe if
 * the give map and the fitler are both thread safe.
 *
 * @author tomyeh
 * @since 5.0.7
 */
public class FilterMap<K, V> extends AbstractMap<K, V> {
	private final Map<K, V> _map;
	private final Filter<V> _filter;

	public FilterMap(Map<K, V> map, Filter<V> filter) {
		if (map == null || filter == null)
			throw new IllegalArgumentException("null");
		_map = map;
		_filter = filter;
	}

	/** Returns the original map being filtered.
	 * That is, the map passed to the constructor.
	 */
	public Map<K, V> getOrigin() {
		return _map;
	}

	//@Override
	public V get(Object key) {
		return _filter.filter(key, _map.get(key));
	}
	//@Override
	public boolean containsKey(Object key) {
		return _map.containsKey(key);
	}

	//@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return new EntrySet();
	}
	private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
		private final Set<Map.Entry<K,V>> _set;

		private EntrySet() {
			_set = _map.entrySet();
		}
		public boolean contains(Object o) {
			return o instanceof Map.Entry
				&& _set.contains(((Entry)o)._me);
		}
		public int size() {
			return _map.size();
		}
		public Iterator<Map.Entry<K,V>> iterator() {
			return new EntryIter(_set.iterator());
		}
		public void clear() {
			_map.clear();
		}

		public String toString() {
			return _set.toString();
		}
	}
	private class EntryIter implements Iterator<Map.Entry<K,V>> {
		private final Iterator<Map.Entry<K,V>> _it;

		private EntryIter(Iterator<Map.Entry<K,V>> it) {
			_it = it;
		}
		public boolean hasNext() {
			return _it.hasNext();
		}
		public Map.Entry<K,V> next() {
			return new Entry(_it.next());
		}
		public void remove() {
			throw new UnsupportedOperationException("readonly");
		}

		public String toString() {
			return _it.toString();
		}
	}
	private class Entry implements Map.Entry<K, V> {
		private final Map.Entry<K,V> _me;

		private Entry(Map.Entry<K,V> me) {
			_me = me;
		}
		public K getKey() {
			return _me.getKey();
		}
		public V getValue() {
			return _filter.filter(_me.getKey(), _me.getValue());
		}
		public V setValue(V value) {
			throw new UnsupportedOperationException("readonly");
		}
		public void clear() {
			_map.clear();
		}

		public String toString() {
			return _me.toString();
		}
		public int hashCode() {
			return _me.hashCode();
		}
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e = (Map.Entry)o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}
	}

	/** Filters the given value (to evaluate when the value is retrieved).
	 */
	public static interface Filter<V> {
		/** Called to filter a value.
		 * @param key the key associated.
		 * Notice that this method is called each time Map.get() is called,
		 * even if the given key is not an instance of K.
		 * @param value the value to filter (i.e., to evaluate)
		 */
		public V filter(Object key, V value);
	}

	//@Override
	public String toString() {
		return _map.toString();
	}
	//@Override
	public int hashCode() {
		return _map.hashCode();
	}
	//@Override
	public boolean equals(Object o) {
		final FilterMap fm;
		return o instanceof FilterMap && _map.equals(fm = ((FilterMap)o))
			&& _filter.equals(fm._filter);
	}
}
