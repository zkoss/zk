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
public class FilterMap extends AbstractMap {
	private final Map _map;
	private final Filter _filter;

	public FilterMap(Map map, Filter filter) {
		if (map == null || filter == null)
			throw new IllegalArgumentException("null");
		_map = map;
		_filter = filter;
	}

	//@Override
	public Object get(Object key) {
		return _filter.filter(key, _map.get(key));
	}
	//@Override
	public boolean containsKey(Object key) {
		return _map.containsKey(key);
	}

	//@Override
	public Set entrySet() {
		return new EntrySet();
	}
	private class EntrySet extends AbstractSet {
		private final Set _set;

		private EntrySet() {
			_set = _map.entrySet();
		}
		public boolean contains(Object o) {
			return o instanceof Entry
				&& _set.contains(((Entry)o)._me);
		}
		public int size() {
			return _map.size();
		}
		public Iterator iterator() {
			return new EntryIter(_set.iterator());
		}

		public String toString() {
			return _set.toString();
		}
		public int hashCode() {
			return _set.hashCode();
		}
		public boolean equals(Object o) {
			return o instanceof EntrySet && _set.equals(((EntrySet)o)._set);
		}
	}
	private class EntryIter implements Iterator {
		private final Iterator _it;

		private EntryIter(Iterator it) {
			_it = it;
		}
		public boolean hasNext() {
			return _it.hasNext();
		}
		public Object next() {
			return new Entry((Map.Entry)_it.next());
		}
		public void remove() {
			throw new UnsupportedOperationException("readonly");
		}

		public String toString() {
			return _it.toString();
		}
		public int hashCode() {
			return _it.hashCode();
		}
		public boolean equals(Object o) {
			return o instanceof EntryIter && _it.equals(((EntryIter)o)._it);
		}
	}
	private class Entry implements Map.Entry {
		private final Map.Entry _me;

		private Entry(Map.Entry me) {
			_me = me;
		}
		public Object getKey() {
			return _me.getKey();
		}
		public Object getValue() {
			return _filter.filter(_me.getKey(), _me.getValue());
		}
		public Object setValue(Object value) {
			throw new UnsupportedOperationException("readonly");
		}

		public String toString() {
			return _me.toString();
		}
		public int hashCode() {
			return _me.hashCode();
		}
		public boolean equals(Object o) {
			final Entry e;
			return o instanceof Entry && _me.equals(((Entry)o)._me);
		}
	}

	/** Filters the given value (to evaluate when the value is retrieved).
	 */
	public static interface Filter {
		/** Called to filter a value.
		 * @param key the key associated
		 * @param value the value to filter (i.e., to evaluate)
		 */
		public Object filter(Object key, Object value);
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
