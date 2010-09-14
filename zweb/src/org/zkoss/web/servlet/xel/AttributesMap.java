/* AttributesMap.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec  6 22:40:23     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.util.Set;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Enumeration;

import static org.zkoss.lang.Generics.cast;

/**
 * A sketetal implementation for Map to wrap something with enumeration of
 * attributes, which must be String.
 *
 * <p>It is mainly used to implement sessionScope and requestScope in EL.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public abstract class AttributesMap extends StringKeysMap<Object> {
	private Set<Map.Entry<String, Object>> _entries;
	public Set<Map.Entry<String, Object>> entrySet() {
		if (_entries == null) {
			_entries = new AbstractSet<Map.Entry<String, Object>>() {
				public int size() {
					return AttributesMap.this.size();
				}
				public boolean contains(Object o) {
					return AttributesMap.this.containsKey(o);
				}
				public boolean isEmpty() {
					return AttributesMap.this.isEmpty();
				}
				public Iterator<Map.Entry<String, Object>> iterator() {
					return cast(new EntryIter());
				}
			};
		}
		return _entries;
	}

	public int size() {
		int sz = 0;
		for (Enumeration<String> e = getKeys(); e.hasMoreElements(); ++sz)
			e.nextElement();
		return sz;
	}
	public boolean isEmpty() {
		return !getKeys().hasMoreElements();
	}
	public Object put(String key, Object val) {
		final Object o = getValue(key);
		setValue(key, val);
		return o;
	}
	public Object remove(Object key) {
		if (key != null && !(key instanceof String))
			return null;
		final String k = (String)key;
		final Object o = getValue(k);
		removeValue(k);
		return o;
	}
}
