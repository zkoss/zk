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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.io.Serializables;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.ListSelectionModel;

/**
 * A skeletal implementation for {@link ListModel} and {@link ListSelectionModel}
 * 
 * @author tomyeh
 */
abstract public class AbstractListModel<E> implements ListModel<E>,
		ListSelectionModel, java.io.Serializable {
	private transient List<ListDataListener> _listeners = new LinkedList<ListDataListener>();

	private LinkedList<Index> _selection = new LinkedList<Index>();

	private boolean _multiple;
	
	/**
	 * Fires a {@link ListDataEvent} for all registered listener (thru
	 * {@link #addListDataListener}.
	 * 
	 * <p>
	 * Note: you can invoke this method only in an event listener.
	 * <p>
	 * To override this method, the overridden method must invoke
	 * <code>super.fireEvent(int, int, int)</code> method to keep the original
	 * behavior to sync the selection status. (since 6.0.0) 
	 */
	protected void fireEvent(int type, int index0, int index1) {
		final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
		
		switch (type) {
		case ListDataEvent.INTERVAL_ADDED:
			insertIndexInterval(index0, index1 - index0+1, true);
			break;
		case ListDataEvent.INTERVAL_REMOVED:
			removeIndexInterval(index0, index1);
			break;
		}
		for (ListDataListener l : _listeners)
			l.onChange(evt);
	}

	// -- ListModel --//
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	private static class Index implements Comparable<Index>, Serializable {
		private int _val;
		private Index(int val) {
			_val = val;
		}
		private int get() {
			return _val;
		}
		private void set(int val) {
			_val = val;
		}
		
		@Override
		public int hashCode() {
			return _val;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Index) {
				return _val == ((Index) obj)._val;
			}
			return false;
		}
		@Override
		public int compareTo(Index o) {
			int val0 = _val;
			int val1 = o._val;
			return (val0 < val1 ? -1 : (val0 == val1 ? 0 : 1));
		}
	}
	
	// ListSelectionModel
	/** {@inheritDoc} */
	@Override
	public int getMinSelectionIndex() {
		return isSelectionEmpty() ? -1 : _selection.getFirst().get();
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSelectionIndex() {
		return isSelectionEmpty() ? -1 : _selection.getLast().get();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSelectedIndex(int index) {
		return _selection.contains(new Index(index));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSelectionEmpty() {
		return _selection.isEmpty();

	}

	/** {@inheritDoc} */
	@Override
	public void addSelectionInterval(int index0, int index1) {
		if (index0 == -1 || index1 == -1) {
			return;
		}
		if (!isMultiple()) {
			index0 = index1;
			_selection.clear();
		}
		boolean changed = false;
		for (int i = index0; i <= index1; i++) {
			if (!_selection.contains(i)) {
				_selection.add(new Index(i));
				changed = true;
			}
		}
		if (changed) {
			Collections.sort(_selection);
			fireEvent(ListDataEvent.SELECTION_CHANGED, index0,
					index1);
		}	
	}

	@Override
	public void removeSelectionInterval(int index0, int index1) {
		if (index0 == -1 || index1 == -1) {
			return;
		}

		boolean changed = false;
		for (int i = index0; i <= index1; i++) {
			if (_selection.remove(new Index(i))) {
				changed = true;
			}
		}
		if (changed) {
			fireEvent(ListDataEvent.SELECTION_CHANGED, index0,
					index1);
		}
	}

	/** {@inheritDoc} */
	public boolean isMultiple() {
		return _multiple;
	}

	/** {@inheritDoc} */
	public void setMultiple(boolean multiple) {
		_multiple = multiple;
	}

	/** {@inheritDoc} */
	public void clearSelection() {
		removeSelectionInterval(getMinSelectionIndex(), getMaxSelectionIndex());
	}

	/**
	 * Returns the selections set.
	 */
	public Set<E> getSelection() {
		HashSet<E> selected = new HashSet<E>();
		int min = getMinSelectionIndex();
		int max = getMaxSelectionIndex();
		for (;min <= max; min++) {
			if (isSelectedIndex(min)) {
				selected.add(getElementAt(min));
			}
		}
		return selected;
	}
	/**
	 * Insert length indices beginning before/after index. This is typically
	 * called to sync the selection model with a corresponding change in the
	 * data model.
	 */
	protected void insertIndexInterval(int index, int length, boolean before) {
		int insertionIndex = before ? index : index + 1;
		for (Index i : _selection) {
			if (i.get() >= insertionIndex)
				i.set(i.get() + length);
		}
	}

	/**
	 * Reorganize the selection index from the specified array.
	 * <p> the original selection will be removed and add the new index.
	 * @param selection the array by which the list will be selected
	 */
	protected void reorganizeIndex(int... selection) {
		_selection.clear();
		for (int i = 0; i < selection.length; i++)
			_selection.add(new Index(selection[i]));
		Collections.sort(_selection);
	}
	/**
	 * Remove the indices in the interval index0,index1 (inclusive) from the
	 * selection model. This is typically called to sync the selection model
	 * width a corresponding change in the data model.
	 */
	protected void removeIndexInterval(int index0, int index1) {
		int length = (index0 - index1) + 1;

		for (Index i : _selection) {
			if (i.get() >= index0)
				i.set(i.get() - length);
		}
	}

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList<ListDataListener>();
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
		clone._listeners = new LinkedList<ListDataListener>();
		clone._selection = (LinkedList<Index>) _selection.clone(); 
		return clone;
	}
}
