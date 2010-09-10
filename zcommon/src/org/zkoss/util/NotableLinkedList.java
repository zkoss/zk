/* NotableLinkedList.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 10 09:03:59 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util;

import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.AbstractSequentialList;

/**
 * Linked list implementation of the <tt>List</tt> interface
 * that provides the callback methods such as {@link #onAdd}, {@link #onSet}
 * and {@link #onRemove}.
 * @author tomyeh
 * @since 6.0.0
 */
public class NotableLinkedList<E> extends AbstractSequentialList<E>
implements List<E>, Cloneable, java.io.Serializable {
	private final LinkedList<E> _list = new LinkedList<E>();

	public NotableLinkedList() {
	}

	@Override
	public int size() {
		return _list.size();
	}
	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListIter(index);
	}

	/** Called each time an new element is about being added into the array.
	 *
	 * <p>Deriving classes usually put checking codes here.
	 * And, throws exception if failure and nothing will be affected.
	 *
	 * @param newElement the element to be added
	 * @param followingElement the elment that will 'follow' the new element.
	 * In other words, newElement will be inserted <b>before</b>
	 * followingElement. If null, it means newElement is appended at the end
	 */
	protected void onAdd(E newElement, E followingElement) {
	}
	/** Called each time an element is about being assigned into the array
	 * and replace an existence one (by ListIterator.set).
	 *
	 * <p>Deriving classes usually put checking codes here.
	 * And, throws exception if failure and nothing will be affected.
	 *
	 * @param newElement the element to be added
	 * @param replaced the element to be replaced
	 */
	protected void onSet(E newElement, E replaced) {
	}
	/** Called each time an element is about being removed from the array.
	 * Deriving classes usually put checking codes here.
	 * And, throws exception if failure.
	 */
	protected void onRemove(E element) {
	}

	private class ListIter implements ListIterator<E> {
		private final ListIterator<E> _iter;
		private E _last;
		private boolean _lastReady;
	
		private ListIter(int index) {
			_iter = _list.listIterator(index);
		}
		@Override
		public void add(E o) {
			final E nxt;
			if (hasNext()) {
				nxt = next();
				previous();
			} else {
				nxt = null;
			}
			onAdd(o, nxt);
			_iter.add(o);
		}
		@Override
		public boolean hasNext() {
			return _iter.hasNext();
		}
		@Override
		public boolean hasPrevious() {
			return _iter.hasPrevious();
		}
		@Override
		public E next() {
			_last = _iter.next();
			_lastReady = true;
			return _last;
		}
		@Override
		public E previous() {
			_last = _iter.previous();
			_lastReady = true;
			return _last;
		}
		@Override
		public int nextIndex() {
			return _iter.nextIndex();
		}
		@Override
		public int previousIndex() {
			return _iter.previousIndex();
		}
		@Override
		public void remove() {
			if (_lastReady)
				onRemove(_last);
			_iter.remove();
			_lastReady = false;
		}
		@Override
		public void set(E o) {
			if (_lastReady)
				onSet(o, _last);
			_iter.set(o);
		}
	}
}
