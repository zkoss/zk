/* StringKeysMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  6 22:35:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el.impl;

import java.util.Map;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * A sketetal implementation for Map to wrap something with enumeration of
 * keys, which must be String.
 *
 * @author tomyeh
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
		public Object setValue(Object val) {
			throw new UnsupportedOperationException("setValue");
			//no need to support it since never used
		}
	}
	/** The iterator class used to iterator the entries in this map.
	 */
	public class EntryIter implements Iterator{
		private final Enumeration _keys = getKeys();
		public boolean hasNext() {
			return _keys.hasMoreElements();
		}
		public Object next() {
			return new Entry((String)_keys.nextElement());
		}
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}
}
