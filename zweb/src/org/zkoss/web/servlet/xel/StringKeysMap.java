/* StringKeysMap.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec  6 22:35:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.util.Map;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * A sketetal implementation for Map to wrap something with enumeration of
 * keys, which must be String.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public abstract class StringKeysMap<V> extends AbstractMap<String, V> {
	//-- Map --//
	public boolean containsKey(Object key) {
		return (key instanceof String) &&  getValue((String)key) != null;
	}
	public V get(Object key) {
		return key instanceof String ? getValue((String)key): null;
	}

	/** Returns the value associated with the specified key. */
	abstract protected V getValue(String key);
	/** Returns an enumeration of keys. */
	abstract protected Enumeration<String> getKeys();
	/** Sets the value associated with the specified key. */
	abstract protected void setValue(String key, V value);
	/** Removes the specified key. */
	abstract protected void removeValue(String key);

	private static class Key {
		protected final String _key;
		private Key(String key) {
			_key = key;
		}
		public boolean equals(Object o) {
			return o instanceof Key && _key.equals(((Key)o)._key);
		}
	}
	private class Entry extends Key implements Map.Entry<String, V> {
		private Entry(String key) {
			super(key);
		}
		public int hashCode() {
			return _key.hashCode();
		}
		public String getKey() {
			return _key;
		}
		public V getValue() {
			return StringKeysMap.this.getValue(_key);
		}
		public V setValue(V value) {
			final V old = getValue();
			StringKeysMap.this.setValue(_key, value);
			return old;
		}
	}
	/** The iterator class used to iterator the entries in this map.
	 */
	public class EntryIter implements Iterator<Entry> {
		private final Enumeration<String> _keys = getKeys();
		private String _key;
		public boolean hasNext() {
			return _keys.hasMoreElements();
		}
		public Entry next() {
			_key = _keys.nextElement();
			return new Entry(_key);
		}
		public void remove() {
			StringKeysMap.this.removeValue(_key);
		}
	}
}
