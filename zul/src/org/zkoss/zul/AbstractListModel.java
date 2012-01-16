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

import java.util.BitSet;
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

	private static final int MIN = -1;

	private static final int MAX = Integer.MAX_VALUE;

	private int _minIndex = MAX;

	private int _maxIndex = MIN;

	private BitSet _value = new BitSet(32);

	private boolean _multiple;

	private int firstChangedIndex = MAX;

	private int lastChangedIndex = MIN;

	private boolean noFireEvent = false;
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

	// ListSelectionModel
	/** {@inheritDoc} */
	@Override
	public int getMinSelectionIndex() {
		return isSelectionEmpty() ? -1 : _minIndex;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSelectionIndex() {
		return _maxIndex;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSelectedIndex(int index) {
		return ((index < _minIndex) || (index > _maxIndex)) ? false : _value
				.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSelectionEmpty() {
		return (_minIndex > _maxIndex);

	}

	/** {@inheritDoc} */
	@Override
	public void addSelectionInterval(int index0, int index1) {
		if (index0 == -1 || index1 == -1) {
			return;
		}
		int clearMin = MAX;
		int clearMax = MIN;
		if (!isMultiple()) {
			index0 = index1;
			clearMin = _minIndex;
			clearMax = _maxIndex;
		}

		int setMin = Math.min(index0, index1);
		int setMax = Math.max(index0, index1);
		changeSelection(clearMin, clearMax, setMin, setMax);
	}

	@Override
	public void removeSelectionInterval(int index0, int index1) {
		if (index0 == -1 || index1 == -1) {
			return;
		}

		int clearMin = Math.min(index0, index1);
		int clearMax = Math.max(index0, index1);
		int setMin = MAX;
		int setMax = MIN;
		changeSelection(clearMin, clearMax, setMin, setMax);
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
		removeSelectionInterval(_minIndex, _maxIndex);
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
		/*
		 * The first new index will appear at insMinIndex and the last one will
		 * appear at insMaxIndex
		 */
		int insMinIndex = (before) ? index : index + 1;
		int insMaxIndex = (insMinIndex + length) - 1;

		/*
		 * Right shift the entire bitset by length, beginning with index-1 if
		 * before is true, index+1 if it's false (i.e. with insMinIndex).
		 */
		for (int i = _maxIndex; i >= insMinIndex; i--) {
			setState(i + length, _value.get(i));
		}

		/*
		 * Initialize the newly inserted indices.
		 */
		for (int i = insMinIndex; i <= insMaxIndex; i++) {
			setState(i, false);
		}
	}

	/**
	 * Reorganize the selection index from the specified array.
	 * <p> the original selection will be removed and add the new index.
	 * @param selection the array by which the list will be selected
	 */
	protected void reorganizeIndex(int... selection) {
		try {
			noFireEvent = true;
			for (int i = _minIndex; i <= _maxIndex; i++)
				clear(i);
			for (int i = 0; i < selection.length; i++)
				set(selection[i]);
		} finally {
			noFireEvent = false;
		}
	}
	/**
	 * Remove the indices in the interval index0,index1 (inclusive) from the
	 * selection model. This is typically called to sync the selection model
	 * width a corresponding change in the data model.
	 */
	protected void removeIndexInterval(int index0, int index1) {
		int rmMinIndex = Math.min(index0, index1);
		int rmMaxIndex = Math.max(index0, index1);
		int gapLength = (rmMaxIndex - rmMinIndex) + 1;

		/*
		 * Shift the entire bitset to the left to close the index0, index1 gap.
		 */
		for (int i = rmMinIndex; i <= _maxIndex; i++) {
			setState(i, _value.get(i + gapLength));
		}
	}
	
	private void setState(int index, boolean state) {
		if (state) {
			set(index);
		} else {
			clear(index);
		}
	}

	private boolean contains(int a, int b, int i) {
		return (i >= a) && (i <= b);
	}

	private void changeSelection(int clearMin, int clearMax, int setMin,
			int setMax) {
		for (int i = Math.min(setMin, clearMin); i <= Math
				.max(setMax, clearMax); i++) {

			boolean shouldClear = contains(clearMin, clearMax, i);
			boolean shouldSet = contains(setMin, setMax, i);

			if (shouldSet && shouldClear) {
				shouldClear = false;
			}

			if (shouldSet) {
				set(i);
			}
			if (shouldClear) {
				clear(i);
			}
		}
		fireSelectionChanged();
	}

	private void fireSelectionChanged() {
		if (lastChangedIndex == MIN || noFireEvent) {
			return;
		}
		/*
		 * Change the values before sending the event to the listeners in case
		 * the event causes a listener to make another change to the selection.
		 */
		int oldFirstChangedIndex = firstChangedIndex;
		int oldLastChangedIndex = lastChangedIndex;
		firstChangedIndex = MAX;
		lastChangedIndex = MIN;
		fireEvent(ListDataEvent.SELECTION_CHANGED, oldFirstChangedIndex,
				oldLastChangedIndex);
	}

	// Update first and last change indices
	private void markAsDirty(int r) {
		firstChangedIndex = Math.min(firstChangedIndex, r);
		lastChangedIndex = Math.max(lastChangedIndex, r);
	}

	// Set the state at this index and update all relevant state.
	private void set(int r) {
		if (_value.get(r)) {
			return;
		}
		_value.set(r);
		markAsDirty(r);

		// Update minimum and maximum indices
		_minIndex = Math.min(_minIndex, r);
		_maxIndex = Math.max(_maxIndex, r);
	}

	// Clear the state at this index and update all relevant state.
	private void clear(int r) {
		if (!_value.get(r)) {
			return;
		}
		_value.clear(r);
		markAsDirty(r);

		// Update minimum and maximum indices
		/*
		 * If (r > minIndex) the minimum has not changed. The case (r <
		 * minIndex) is not possible because r'th value was set. We only need to
		 * check for the case when lowest entry has been cleared, and in this
		 * case we need to search for the first value set above it.
		 */
		if (r == _minIndex) {
			for (_minIndex = _minIndex + 1; _minIndex <= _maxIndex; _minIndex++) {
				if (_value.get(_minIndex)) {
					break;
				}
			}
		}
		/*
		 * If (r < maxIndex) the maximum has not changed. The case (r >
		 * maxIndex) is not possible because r'th value was set. We only need to
		 * check for the case when highest entry has been cleared, and in this
		 * case we need to search for the first value set below it.
		 */
		if (r == _maxIndex) {
			for (_maxIndex = _maxIndex - 1; _minIndex <= _maxIndex; _maxIndex--) {
				if (_value.get(_maxIndex)) {
					break;
				}
			}
		}
		/*
		 * Performance note: This method is called from inside a loop in
		 * changeSelection() but we will only iterate in the loops above on the
		 * basis of one iteration per deselected cell - in total. i.e. the next
		 * time this method is called the work of the previous deselection will
		 * not be repeated.
		 * 
		 * We also don't need to worry about the case when the min and max
		 * values are in their unassigned states. This cannot happen because
		 * this method's initial check ensures that the selection was not empty
		 * and therefore that the minIndex and maxIndex had 'real' values.
		 * 
		 * If we have cleared the whole selection, set the minIndex and maxIndex
		 * to their cannonical values so that the next set command always works
		 * just by using Math.min and Math.max.
		 */
		if (isSelectionEmpty()) {
			_minIndex = MAX;
			_maxIndex = MIN;
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
		clone._value = (BitSet) _value.clone(); 
		return clone;
	}
}
