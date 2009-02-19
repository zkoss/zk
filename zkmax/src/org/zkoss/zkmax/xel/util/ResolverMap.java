/* ResolverMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 17 11:24:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.zkoss.xel.VariableResolver;

/**
 * A Map interface on top of {@link VariableResolver}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ResolverMap implements Map {
	private VariableResolver _resolver;

	public ResolverMap(VariableResolver resolver) {
		_resolver = resolver;
	}

	public boolean containsKey(Object key) {
		return get(key) != null;
	}
	public Object get(Object key) {
		return _resolver.resolveVariable((String)key);
	}

	/** Always returns an empty set, no matter any variable is defined.
	 */
	public Set entrySet() {
		return Collections.EMPTY_SET;
	}
	public void clear() {
		throw new UnsupportedOperationException();
	}
	/** Always returns false, no matter any variable is defined.
	 */
	public boolean containsValue(Object value) {
		return false;
	}
	/** Always returns true, no matter any variable is defined.
	 */
	public boolean isEmpty() {
		return true;
	}
	/** Always returns an empty set, no matter any variable is defined.
	 */
	public Set keySet() {
		return Collections.EMPTY_SET;
	}
	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}
	public void putAll(Map map) {
		throw new UnsupportedOperationException();
	}
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}
	/** Always returns 0, no matter any variable is defined.
	 */
	public int size() {
		return 0;
	}
	/** Always returns an empty collection, no matter any variable is defined.
	 */
	public Collection values() {
		return Collections.EMPTY_LIST;
	}
}
