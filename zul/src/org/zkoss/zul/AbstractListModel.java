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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.io.Serializables;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.SelectionControl;

/**
 * A skeletal implementation for {@link ListModel} and {@link Selectable}
 * 
 * @author tomyeh
 */
abstract public class AbstractListModel<E> implements ListModel<E>,
Selectable<E>, java.io.Serializable, Pageable {
	private transient List<ListDataListener> _listeners = new ArrayList<ListDataListener>();

	/** The current selection. */
	protected transient Set<E> _selection;
	private SelectionControl<E> _ctrl;
	private boolean _multiple;

	protected AbstractListModel() {
		_selection = newEmptySelection();
		_ctrl = new DefaultSelectionControl<E>(this);
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
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	/** {@inheritDoc} */
	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	//Selectable//
	/** {@inheritDoc} */
	public Set<E> getSelection() {
		return Collections.unmodifiableSet(_selection);
	}
	/** {@inheritDoc} */
	public void setSelection(Collection<? extends E> selection) {
		if (isSelectionChanged(selection)) {
			if (!_multiple && selection.size() > 1)
				throw new IllegalArgumentException("Only one selection is allowed, not "+selection);
			_selection.clear();
			_selection.addAll(selection);
			if (selection.isEmpty()) {
				fireSelectionEvent(null);
			} else
				fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}
	private boolean isSelectionChanged(Collection<? extends E> selection) {
		if (_selection.size() != selection.size())
			return true;

		for (final E e: selection)
			if (!_selection.contains(e))
				return true;
		return false;
	}

	/** {@inheritDoc} */
	public boolean isSelected(Object obj) {
		return !isSelectionEmpty()
				&& (_selection.size() == 1 ? Objects
						.equals(_selection.iterator().next(), obj)
						 : _selection.contains(obj));
	}
	/** {@inheritDoc} */
	public boolean isSelectionEmpty() {
		return _selection.isEmpty();
	}

	/** {@inheritDoc} */
	public boolean addToSelection(E obj) {
		if (_selection.add(obj)) {
			if (!_multiple) {
				_selection.clear();
				_selection.add(obj);
			}
			fireSelectionEvent(obj);
			return true;
		}
		return false;
	}
	/** {@inheritDoc} */
	public boolean removeFromSelection(Object obj) {
		if (_selection.remove(obj)) {
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
			return true;
		}
		return false;
	}
	/** {@inheritDoc} */
	public void clearSelection() {
		if (!_selection.isEmpty()) {
			_selection.clear();
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}

	/**
	 * Selectable's implementor use only.
	 * <p> Fires a selection event for component to scroll into view. The override
	 * subclass must put the index0 of {@link #fireEvent(int, int, int)} as 
	 * the view index to scroll. By default, the value -1 is assumed which means
	 * no scroll into view.
	 * <p> The method is invoked when both methods are invoked. {@link #addToSelection(Object)}
	 * and {@link #setSelection(Collection)}.
	 * @param e selected object.
	 */
	protected void fireSelectionEvent(E e) {
		fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
	}
	
	/**Removes the selection of the given collection.
	 */
	protected void removeAllSelection(Collection<?> c) {
		// B60-ZK-1126
		// Notify selection has been changed
		if (_selection.removeAll(c)) {
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}
	/**Removes the selection that doesn't belong to the given collection.
	 */
	protected void retainAllSelection(Collection<?> c) {
		// B60-ZK-1126
		// Notify selection has been changed
		if (_selection.retainAll(c)) {
			fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
		}
	}

	/** {@inheritDoc} */
	public boolean isMultiple() {
		return _multiple;
	}
	/** {@inheritDoc} */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			fireEvent(ListDataEvent.MULTIPLE_CHANGED, -1, -1);

			if (!multiple && _selection.size() > 1) {
				E v = _selection.iterator().next();
				_selection.clear();
				_selection.add(v);
				fireEvent(ListDataEvent.SELECTION_CHANGED, -1, -1);
			}
		}
	}

	public void setSelectionControl(SelectionControl ctrl) {
		_ctrl = ctrl;
	}

	public SelectionControl getSelectionControl() {
		return _ctrl;
	}

	/**
	 * A default selection control implementation for {@link AbstractListModel},
	 * by default it assumes all elements are selectable.
	 * <p>Note: the implementation is not used for a huge data model, if in this case,
	 * please implement your own one to speed up.</p>
	 * @since 8.0.0
	 */
	public static class DefaultSelectionControl<E> implements SelectionControl<E> {
		private AbstractListModel model;
		public DefaultSelectionControl(AbstractListModel model) {
			this.model = model;
		}
		public boolean isSelectable(E e) {
			return true;
		}
		public void setSelectAll(boolean selectAll) {
			if (selectAll) {
				List all = new LinkedList();
				for (int i = 0, j = model.getSize(); i < j; i++) {
					E o = (E) model.getElementAt(i);
					if (isSelectable(o)) // check whether it can be selectable or not
						all.add(o);
				}

				// avoid scroll into view at client side.
				model.fireEvent(ListDataEvent.DISABLE_CLIENT_UPDATE, -1, -1);

				if (model instanceof AbstractListModel)
					try {
						((Selectable)model).setSelection(all);
					} finally {
						model.fireEvent(ListDataEvent.ENABLE_CLIENT_UPDATE, -1, -1);
					}
			} else {
				((Selectable)model).clearSelection();
			}
		}
		public boolean isSelectAll() {
			for (int i = 0, j = model.getSize(); i < j; i++) {
				E o = (E) model.getElementAt(i);
				if (isSelectable(o) && !((Selectable)model).isSelected(o))
					return false;
			}
			return true;
		}
	}

	/** Instantiation an empty set of the section.
	 * It is used to initialize {@link #_selection}.
	 * <p>By default, it instantiates an instance of LinkedHashSet.
	 * The deriving class might override to instantiate a different class.
	 */
	protected Set<E> newEmptySelection() {
		return new LinkedHashSet<E>();
	}
	/** Writes {@link #_selection}.
	 * <p>Default: write it directly. Override it if E is not serializable.
	 */
	protected void writeSelection(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.writeObject(_selection);
	}
	/** Reads back {@link #_selection}.
	 * <p>Default: write it directly. Override it if E is not serializable.
	 */
	@SuppressWarnings("unchecked")
	protected void readSelection(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		_selection = (Set<E>)s.readObject();
	}

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		writeSelection(s);
		Serializables.smartWrite(s, _listeners);
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		readSelection(s);
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
	
	// Pageable //
	private int _pageSize = 20; // same default as paging
	private int _activePage = 0; // same default as paging

	// Pageable // ZK-1696
	public int getPageSize() {
		return _pageSize;
	}

	public void setPageSize(int size) throws WrongValueException {
		if (size < 0) {
			throw new WrongValueException("page size should >= 0");
		}
		_pageSize = size;
	}

	public int getPageCount() {
		int size = getSize();
		if(size > 0){
			int pageCount = size / _pageSize;
			if(size % _pageSize == 0){
				return pageCount;
			} else{
				return pageCount + 1;
			}
		} else{
			return 1;
		}
	}

	public int getActivePage() {
		return _activePage;
	}

	public void setActivePage(int pg) throws WrongValueException {
		if (pg < 0) {
			throw new WrongValueException("active page index should >= 0");
		}
		_activePage = pg;
	}
}
