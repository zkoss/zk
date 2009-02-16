/* AttributesMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  6 22:40:23     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.util.Set;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * A sketetal implementation for Map to wrap something with enumeration of
 * attributes, which must be String.
 *
 * <p>It is mainly used to implement sessionScope and requestScope in EL.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public abstract class AttributesMap extends StringKeysMap {
	private Set _entries;
	public Set entrySet() {
		if (_entries == null) {
			_entries = new AbstractSet() {
				public int size() {
					return AttributesMap.this.size();
				}
				public boolean contains(Object o) {
					return AttributesMap.this.containsKey(o);
				}
				public boolean isEmpty() {
					return AttributesMap.this.isEmpty();
				}
				public Iterator iterator() {
					return new EntryIter();
				}
			};
		}
		return _entries;
	}

	public int size() {
		int sz = 0;
		for (Enumeration e = getKeys(); e.hasMoreElements(); ++sz)
			e.nextElement();
		return sz;
	}
	public boolean isEmpty() {
		return !getKeys().hasMoreElements();
	}
	public Object put(Object key, Object val) {
		final Object o = getValue((String)key);
		setValue((String)key, val);
		return o;
	}
	public Object remove(Object key) {
		final Object o = getValue((String)key);
		removeValue((String)key);
		return o;
	}
}
