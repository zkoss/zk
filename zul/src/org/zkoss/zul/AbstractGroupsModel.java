/* AbstractGroupsModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  2 08:45:01     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.zkoss.io.Serializables;

import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * A skeletal implementation for {@link GroupsModel}.
 * <p> Implements {@link Selectable} interface to handle the selection status.
 * (Since 6.0.0)
 * <p>Generics:
 * <dl>
 * <dt>D</dt><dd>The class of each data</dd>
 * <dt>H</dt><dd>The class of each group header</dd>
 * <dt>F</dt><dd>The class of each group footer</dd>
 * <dt>E</dt><dd>The class of each selection. It is the common base class
 * of D, H, F. In other words, D, H and F must extend from E.</dd>
 * </dl>
 * @author tomyeh
 * @since 3.5.0
 * @see Selectable
 */
abstract public class AbstractGroupsModel<D, H, F, E> implements GroupsModel<D, H, F>,
Selectable<E>, java.io.Serializable {
	private transient List<GroupsDataListener> _listeners = new LinkedList<GroupsDataListener>();
	
	/** The current selection. */
	protected transient Set<E> _selection;
	private boolean _multiple;
	
	protected AbstractGroupsModel() {
		_selection = newEmptySelection();
	}
	/** Fires a {@link GroupsDataEvent} for all registered listener
	 * (thru {@link #addGroupsDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int groupIndex, int index0, int index1) {
		final GroupsDataEvent evt =
			new GroupsDataEvent(this, type, groupIndex, index0, index1);
		for (GroupsDataListener l: _listeners)
			l.onChange(evt);
	}

	//-- GroupsModel --//
	public void addGroupsDataListener(GroupsDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	public void removeGroupsDataListener(GroupsDataListener l) {
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
			if (selection.isEmpty()) {
				fireSelectionEvent(null);
			} else
				fireSelectionEvent(selection.iterator().next());
		}
	}
	/** {@inheritDoc} */
	@Override
	public boolean isSelected(Object obj) {
		return _selection.contains(obj);
	}
	/** {@inheritDoc} */
	@Override
	public boolean isSelectionEmpty() {
		return _selection.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public void addToSelection(E obj) {
		if (_selection.add(obj)) {
			if (!_multiple) {
				_selection.clear();
				_selection.add(obj);
			}
			fireSelectionEvent(obj);
		}
	}
	/** {@inheritDoc} */
	@Override
	public boolean removeFromSelection(Object obj) {
		if (_selection.remove(obj)) {
			fireEvent(GroupsDataEvent.SELECTION_CHANGED, -1, -1, -1);
			return true;
		}
		return false;
	}
	/** {@inheritDoc} */
	@Override
	public void clearSelection() {
		if (!_selection.isEmpty()) {
			_selection.clear();
			fireEvent(GroupsDataEvent.SELECTION_CHANGED, -1, -1, -1);
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
		fireEvent(GroupsDataEvent.SELECTION_CHANGED, -1, -1, -1);
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
		if (_multiple != multiple) {
			_multiple = multiple;
			fireEvent(GroupsDataEvent.MULTIPLE_CHANGED, -1, -1, -1);

			if (!multiple && _selection.size() > 1) {
				E v = _selection.iterator().next();
				_selection.clear();
				_selection.add(v);
				fireEvent(GroupsDataEvent.SELECTION_CHANGED, -1, -1, -1);
			}
		}
	}

	/** Instantiation an empty set of the section.
	 * It is used to initialize {@link #_selection}.
	 * <p>By default, it instantiates an instance of LinkedHashMap.
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
	
	//Serializable//
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
		_listeners = new LinkedList<GroupsDataListener>();
		Serializables.smartRead(s, _listeners);
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		final AbstractGroupsModel clone;
		try {
			clone = (AbstractGroupsModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._listeners = new LinkedList<GroupsDataListener>();
		return clone;
	}
}
