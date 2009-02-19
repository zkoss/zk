/* DualCollection.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 21:29:38     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.AbstractCollection;

/**
 * A combination of two collections into a collection.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DualCollection extends AbstractCollection
implements java.io.Serializable {
	private final Collection _first, _second;

	/** Returns a collection by combining two collections.
	 * It checks whether any of them is null, or equals. And, returns
	 * the non-null one if another is null.
	 * If both null, it returns null.
	 */
	public static final
	Collection combine(Collection first, Collection second) {
		if (first == second) //we don't use equals to have better performance
			return first;

		if (first != null)
			if (second != null)
				return new DualCollection(first, second);
			else
				return first;
		else
			return second;
	}
	/** Constructor.
	 * It is better to use {@link #combine} instead of this method
	 * since it checks whether any of them is null or equals.
	 */
	public DualCollection(Collection first, Collection second) {
		_first = first != null ? first: Collections.EMPTY_LIST;
		_second = second != null ? second: Collections.EMPTY_LIST;
	}

	//Collection//
	public int size() {
		return _first.size() + _second.size();
	}
	public Iterator iterator() {
		return new Iter();
	}
	private class Iter implements Iterator {
		private Iterator _it;
		private boolean _bSecond;

		private Iter() {
			_it = _first.iterator();
		}

		public boolean hasNext() {
			return _it.hasNext() || (!_bSecond && !_second.isEmpty());
		}
		public Object next() {
			if (!_bSecond && !_it.hasNext()) {
				_it = _second.iterator();
				_bSecond = true;
			}
			return _it.next();
		}
		public void remove() {
			_it.remove();
		}
	}
}
