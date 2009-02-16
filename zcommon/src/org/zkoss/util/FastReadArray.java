/* FastReadArray.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 17 19:19:19     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Array;

import org.zkoss.lang.Objects;

/**
 * An array of objects that are fast to read but a bit slower to add and
 * remove.
 *
 * <p>It is thread-safe.
 *
 * <p>Typical use:
 * <pre><code>Object[] ary = fra.toArray();
 *for (int j = 0; j < ary.length; ++J)
 *  <i>whatever</i>;</code></pre>
 *
 * @author tomyeh
 * @since 3.0.6
 */
public class FastReadArray implements java.io.Serializable, Cloneable {
	private Object[] _ary;

	/** Constructs an array of Object.
	 */
	public FastReadArray() {
		_ary = new Object[0];
	}
	/** Constructs an array of the specified class.
	 */
	public FastReadArray(Class klass) {
		_ary = (Object[])Array.newInstance(klass, 0);
	}
	/** Returns the array (never null).
	 * To read, you shall store the return value in a local variable
	 * and then iterate thru it.
	 *
	 * <p>Note: the return array is readonly. Don't modify the value
	 * of any element.
	 */
	public Object[] toArray() {
		return _ary;
	}
	/** Returns if it is empty.
	 */
	public boolean isEmpty() {
		return _ary.length == 0;
	}
	/** Returns the size.
	 */
	public int size() {
		return _ary.length;
	}

	/** Adds an object.
	 */
	synchronized public void add(Object val) {
		Object[] ary = (Object[])ArraysX.resize(_ary, _ary.length + 1);
		ary[_ary.length] = val;
		_ary = ary;
	}
	/** Removes an object.
	 * @return whether the object is removed successfully (i.e., found).
	 */
	synchronized public boolean remove(Object val) {
		final List l = new LinkedList();
		boolean found = false;
		for (int j = 0; j < _ary.length; ++j)
			if (found || !Objects.equals(val, _ary[j]))
				l.add(_ary[j]);
			else
				found = true;

		if (found)
			_ary = (Object[])l.toArray((Object[])
				Array.newInstance(_ary.getClass().getComponentType(), l.size()));
		return found;
	}
	/** Removes the object(s) that matches the specified condition.
	 * By match we mean {@link Comparable#compareTo} returns 0.
	 * In other words, this method invokes val.compareTo() against
	 * each element in this array.
	 *
	 * @param atMostOne whether to remove the first matched object only.
	 * If true, only the first matched object, if any, is removed.
	 * If false, all matched object are removed.
	 */
	synchronized public boolean removeBy(Comparable val, boolean atMostOne) {
		final List l = new LinkedList();
		boolean found = false;
		for (int j = 0; j < _ary.length; ++j)
			if ((atMostOne && found) || val.compareTo(_ary[j]) != 0)
				l.add(_ary[j]);
			else
				found = true;

		if (found)
			_ary = (Object[])l.toArray((Object[])
				Array.newInstance(_ary.getClass().getComponentType(), l.size()));
		return found;
	}

	public Object clone() {
		//No need to deep copy _ary since it is considered as readonly
		try {
			return super.clone();
		}catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	//Object//
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FastReadArray))
			return false;

		return Objects.equals(_ary, ((FastReadArray)o)._ary);
	}
	public int hashCode() {
		return Objects.hashCode(_ary);
	}
	public String toString() {
		return Objects.toString(_ary);
	}
}
