/* AbstractListModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:19:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.Serializable;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.io.Serializables;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * A skeletal implementation for {@link ListModel} and {@link Selectable}
 * 
 * @author tomyeh
 */
abstract public class AbstractListModel<E> implements ListModel<E>,
Selectable<E>, java.io.Serializable {
	private transient List<ListDataListener> _listeners = new ArrayList<ListDataListener>();

	/** The current selection. */
	protected Set<E> _selection;
	private boolean _multiple;

	protected AbstractListModel() {
		_selection = newEmptySelection();
	}	

	/**
	 * Fires a {@link ListDataEvent} for all registered listener (thru
	 * {@link #addListDataListener}.
	 * 
	 * <p>
	 * Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int index0, int index1) {
		final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
		for (ListDataListener l : _listeners)
			l.onChange(evt);
	}

	// -- ListModel --//
	/** {@inheritDoc} */
	@Override
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	/** {@inheritDoc} */
	@Override
	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	//Selectable//
	/** {@inheritDoc} */
	@Override
	public Set<E> getSelection() {
		return Collections.unmodifiableSet(_selection);
	}
	/** {@inheritDoc} */
	@Override
	public void setSelection(Collection<? extends E> selection) {
		if (!_selection.equals(selection)) {
			if (!_multiple && _selection.size() > 1)
				throw new IllegalArgumentException("Only one selection is allowed, not "+selection);
			_selection.clear();
			_selection.addAll(selection);
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}
	/** {@inheritDoc} */
	@Override
	public boolean isSelected(E obj) {
		return _selection.contains(obj);
	}

	/** {@inheritDoc} */
	@Override
	public void addToSelection(E obj) {
		if (_selection.add(obj)) {
			if (!_multiple) {
				_selection.clear();
				_selection.add(obj);
			}
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}
	/** {@inheritDoc} */
	@Override
	public boolean removeFromSelection(Object obj) {
		if (_selection.remove(obj)) {
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
			return true;
		}
		return false;
	}
	/** {@inheritDoc} */
	@Override
	public void clearSelection() {
		if (!_selection.isEmpty()) {
			_selection.clear();
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}

	/**Removes the selection of the given collection.
	 */
	protected void removeAllSelection(Collection<?> c) {
		_selection.removeAll(c);
	}
	/**Removes the selection that doesn't belong to the given collection.
	 */
	protected void retainAllSelection(Collection<?> c) {
		_selection.retainAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMultiple() {
		return _multiple;
	}
	/** {@inheritDoc} */
	@Override
	public void setMultiple(boolean multiple) {
		_multiple = multiple;
	}

	/** Instantiation an empty set of the section.
	 * It is used to initialize {@link #_selection}.
	 * <p>By default, it instantiates an instance of LinkedHashMap.
	 * The deriving class might override to instantiate a different class.
	 */
	protected Set<E> newEmptySelection() {
		return new LinkedHashSet<E>();
	}

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new ArrayList<ListDataListener>();
		Serializables.smartRead(s, _listeners);
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		final AbstractListModel clone;
		try {
			clone = (AbstractListModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._listeners = new ArrayList<ListDataListener>();
		clone._selection = clone.newEmptySelection();
		clone._selection.addAll(_selection);
		return clone;
	}
}
