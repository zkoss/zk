/* ComodifiableIterator.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 11:53:25 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * A comodifiable itnerator. Used internally.
 * @author tomyeh
 */
/*package*/ class ComodifiableIterator<F, T> implements Iterator<T> {
	private final List<F> _visited = new LinkedList<F>();
	private List<F> _lastVisited;
	private final Collection<F> _col;
	private Iterator<F> _it;
	private F _next;
	private Converter<F, T> _converter;
	private boolean _nextAvail;

	@SuppressWarnings("unchecked")
	/*package*/ ComodifiableIterator(Collection<F> col, Converter<F, T> converter) {
		_col = col;
		_it = col.iterator();
		_converter = converter != null ? converter: _identityConverter;
	}
	public boolean hasNext() {
		//Note: we cannot just check hasNext() since it does not throw
		//ConcurrentModificationException, so if _col is cleared, hasNext()
		//might still return true! Moreover, hasNext() might return false
		//while there is more
		if (_nextAvail)
			return true;
		while (!_col.isEmpty()) { //isEmpty is reliable and empty is a common case
			final F o;
			try {
				o = _it.next();
			} catch (java.util.NoSuchElementException ex) {
				return false;
			} catch (java.util.ConcurrentModificationException ex) {
				_lastVisited = new LinkedList<F>(_visited); //make a copy
				_it = _col.iterator();
				continue; //do it again
			}
			if (!removeFromLastVisited(o)) { //not visited before
				_visited.add(o);
				_next = o;
				return _nextAvail = true;
			}
		}
		return false;
	}
	public T next() {
		if (_nextAvail) {
			_nextAvail = false;
			return _converter.convert(_next);
		}
		for (;;) {
			final F o;
			try {
				o = _it.next();
			} catch (java.util.ConcurrentModificationException ex) {
				_lastVisited = new LinkedList<F>(_visited); //make a copy
				_it = _col.iterator();
				continue; //do it again
			}
			if (!removeFromLastVisited(o)) { //not visited before
				_visited.add(o);
				return _converter.convert(o);
			}
		}
	}
	private boolean removeFromLastVisited(F o) {
		if (_lastVisited != null)
			for (Iterator<F> it = _lastVisited.iterator(); it.hasNext();) {
				if (it.next() == o) { //not equals (more retricted)
					it.remove();
					return true;
				}
			}
		return false;
	}
	public void remove() {
		_it.remove();
	}

	private static final Converter _identityConverter = new Converter() {
		public Object convert(Object o) {
			return o;
		}
	};
}
