/* IdentityHashSet.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 13 11:18:11  2002, Created by tomyeh

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Collection;
import java.util.Set;
import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.zkoss.lang.Objects;

/**
 * Like java.util.InternalHashMap, it uses == and System.identityHashCode
 * for doing HashSet.
 *
 * @author tomyeh
 * @see IdentityComparator
 */
public class IdentityHashSet<T> extends AbstractSet<T>
implements Set<T>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

    private transient IdentityHashMap<T, Object> _map;

    /**
     * Constructs a new, empty set; the backing <tt>IdentityHashMap</tt>
     * instance has default capacity (32).
     */
    public IdentityHashSet() {
		_map = new IdentityHashMap<T, Object>();
	}

	/**
	 * Constructs a new set containing the elements in the specified
	 * collection.
	 *
	 * @param c the collection whose elements are to be placed into this set.
	 * @throws NullPointerException if the specified collection is null.
	 */
	public IdentityHashSet(Collection<T> c) {
		_map = new IdentityHashMap<T, Object>(Math.max((c.size()*4)/3, 16));
		addAll(c);
	}

	/**
     * Constructs a new, empty set with the specified expected maximum size.
	 *
     * @param expectedMaxSize the expected maximum size of the map.
     * @throws IllegalArgumentException if <tt>expectedMaxSize</tt> is negative
	 */
	public IdentityHashSet(int expectedMaxSize) {
		_map = new IdentityHashMap<T, Object>(expectedMaxSize);
	}

	//-- Set --//
	public Iterator<T> iterator() {
		return _map.keySet().iterator();
	}
	public int size() {
		return _map.size();
	}
	public boolean isEmpty() {
		return _map.isEmpty();
	}
	public boolean contains(Object o) {
		return _map.containsKey(o);
	}
	public boolean add(T o) {
		return _map.put(o, Objects.UNKNOWN)==null;
	}
	public boolean remove(Object o) {
		return _map.remove(o)==Objects.UNKNOWN;
	}
	public void clear() {
		_map.clear();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try { 
			IdentityHashSet<T> newSet = (IdentityHashSet<T>)super.clone();
			newSet._map = (IdentityHashMap<T, Object>)_map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) { 
			throw new InternalError();
		}
	}

	//-- Serializable --//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();

		// Write out size
		s.writeInt(_map.size());

		// Write out all elements in the proper order.
		for (T key: _map.keySet())
			s.writeObject(key);
	}
	@SuppressWarnings("unchecked")
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();

        // Read in size (number of Mappings)
        int size = s.readInt();

		// Read in IdentityHashMap capacity and load factor and create backing IdentityHashMap
		_map = new IdentityHashMap<T, Object>((size*4)/3);
			// Allow for 33% growth (i.e., capacity is >= 2* size()).

		// Read in all elements in the proper order.
		for (int i=0; i<size; i++) {
			Object e = s.readObject();
			_map.put((T)e, Objects.UNKNOWN);
		}
	}
}
