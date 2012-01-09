/* WeakHashSet.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2011 12:15:42 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A {@link Set} that each entry is weakly referenced.
 * @author henrichen
 * @since 6.0.0
 */
public class WeakHashSet<E> implements Set<E>, Serializable{
	private static final long serialVersionUID = 1463169907348730644L;
	private final Map<E, Object> _map; 
	private final Set<E> _inner;

	public WeakHashSet(int initialCapacity) {
		_map = new WeakHashMap<E, Object>(initialCapacity);
		_inner = _map.keySet();
	}
	public WeakHashSet() {
		_map = new WeakHashMap<E, Object>();
		_inner = _map.keySet();
	}
	
	public int size() {
		return _inner.size();
	}

	public boolean isEmpty() {
		return _inner.isEmpty();
	}

	public boolean contains(Object o) {
		return _inner.contains(o);
	}

	public Iterator<E> iterator() {
		return _inner.iterator();
	}

	public Object[] toArray() {
		return _inner.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _inner.toArray(a);
	}

	public boolean add(E e) {
		final boolean result = !_map.containsKey(e); 
		_map.put(e, null);
		return result;
	}

	public boolean remove(Object o) {
		return _inner.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return _inner.contains(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		return _inner.addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return _inner.retainAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return _inner.removeAll(c);
	}

	public void clear() {
		_inner.clear();
	}
}
