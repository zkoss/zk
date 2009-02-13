/* StringKeysMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  6 22:35:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
public abstract class StringKeysMap extends AbstractMap {
	//-- Map --//
	public boolean containsKey(Object key) {
		return (key instanceof String) &&  getValue((String)key) != null;
	}
	public Object get(Object key) {
		return key instanceof String ? getValue((String)key): null;
	}

	/** Returns the value associated with the specified key. */
	abstract protected Object getValue(String key);
	/** Returns an enumeration of keys. */
	abstract protected Enumeration getKeys();
	/** Sets the value associated with the specified key. */
	abstract protected void setValue(String key, Object value);
	/** Removes the specified key. */
	abstract protected void removeValue(String key);

	private class Entry implements Map.Entry {
		private final String _key;
		private Entry(String key) {
			_key = key;
		}
		public boolean equals(Object o) {
			return (o instanceof Entry)
				&& _key.equals(((Entry)o)._key);
		}
		public int hashCode() {
			return _key.hashCode();
		}
		public Object getKey() {
			return _key;
		}
		public Object getValue() {
			return StringKeysMap.this.getValue(_key);
		}
		public Object setValue(Object value) {
			final Object old = getValue();
			StringKeysMap.this.setValue(_key, value);
			return old;
		}
	}
	/** The iterator class used to iterator the entries in this map.
	 */
	public class EntryIter implements Iterator{
		private final Enumeration _keys = getKeys();
		private String _key;
		public boolean hasNext() {
			return _keys.hasMoreElements();
		}
		public Object next() {
			_key = (String)_keys.nextElement();
			return new Entry(_key);
		}
		public void remove() {
			StringKeysMap.this.removeValue(_key);
		}
	}
}
