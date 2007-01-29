/* ListModelSet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 01 14:38:43     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import java.util.Set;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>This is the {@link ListModel} as a {@link java.util.Set} to be used with {@link Listbox}.
 * Add or remove the contents of this model as a Set would cause the associated Listbox to change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelSet extends AbstractListModel implements Set {
	protected List _list;
	protected Set _set;

	/**
	 * new an instance which accepts a "live" Set as its inner Set. Any change to this
	 * ListModelSet will change to the passed in "live" Set.
	 * @param set the inner Set storage.
	 */
	public static ListModelSet instance(Set set) {
		return new ListModelSet(set);
	}

	/**
	 * <p>Constructor, unlike other Set implementation, the passed in Set is a "live" set inside
	 * this ListModelSet; i.e., when you add or remove items from this ListModelSet,
	 * the inner "live" set would be changed accordingly.</p>
	 * @param set the inner "live" set that would be added and/or removed accordingly
	 * when you add and/or remove item to this ListModelSet.
	 */
	private  ListModelSet(Set set) {
		_set = set;
		_list = new ArrayList(set);
	}
	
	/**
	 * Constructor.
	 */
	public ListModelSet() {
		_set = new LinkedHashSet();
		_list = new ArrayList();
	}

	/**
	 * Constructor.
	 */
	public ListModelSet(Collection c) {
		_set = new LinkedHashSet(c);
		_list = new ArrayList(c);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelSet.
	 */
	public ListModelSet(int initialCapacity) {
		_set = new LinkedHashSet(initialCapacity);
		_list = new ArrayList(initialCapacity);
	}

	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 * @param loadFactor the loadFactor to increase capacity of this ListModelMap.
	 */
	public ListModelSet(int initialCapacity, float loadFactor) {
		_set = new LinkedHashSet(initialCapacity, loadFactor);
		_list = new ArrayList(initialCapacity);
	}

	/**
	 * Get the inner real set.
	 */	
	public Set getInnerSet() {
		return _set;
	}
	
	//-- ListModel --//
	public int getSize() {
		return _set.size();
	}
	
	public Object getElementAt(int j) {
		return _list.get(j);
	}
	
	//-- Set --//
 	public boolean add(Object o) {
 		if (!_set.contains(o)) {
			int i1 = _set.size();
			boolean ret = _set.add(o);
			_list.add(o);
			fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
			return ret;
		}
		return false;
	}

	public boolean addAll(Collection c) {
		int begin = _set.size();
		int added = 0;
		for(final Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			if (_set.contains(o)) {
				continue;
			}
			_list.add(o);
			_set.add(o);
			++added;
		}
		if (added > 0) {
			fireEvent(ListDataEvent.INTERVAL_ADDED, begin, begin+added-1);
			return true;
		}
		return false;
	}
	
	public void clear() {
		int i2 = _set.size() - 1;
		if (i2 < 0) {
			return;
		}
		_list.clear();
		_set.clear();
		fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
	}
	
	public boolean contains(Object elem) {
		return _set.contains(elem);
	}
	
	public boolean containsAll(Collection c) {
		return _set.containsAll(c);
	}
	
	public boolean equals(Object o) {
		return _set.equals(o);
	}
	
	public int hashCode() {
		return _set.hashCode();
	}
		
	public boolean isEmpty() {
		return _set.isEmpty();
	}
    
    public Iterator iterator() {
    	return _set.iterator();
    }
	
	public boolean remove(Object o) {
		if (_set.contains(o)) {
			int index = _list.indexOf(o);
			_list.remove(index);
			boolean ret = _set.remove(o);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return ret;
		}
		return false;
	}
	
	public boolean removeAll(Collection c) {
		if (_set == c || this == c) { //special case
			clear();
			return true;
		}
		_list.removeAll(c);
		return removePartial(c, true);
	}

	public boolean retainAll(Collection c) {
		if (_set == c || this == c) { //special case
			return false;
		}
		_list.retainAll(c);
		return removePartial(c, false);
	}
	
	private boolean removePartial(Collection c, boolean isRemove) {
		int sz = c.size();
		int removed = 0;
		int retained = 0;
		int index = 0;
		int begin = -1;
		for(final Iterator it = _set.iterator(); 
			it.hasNext() && (!isRemove || removed < sz) && (isRemove || retained < sz); ++index) {
			Object item = it.next();
			if (c.contains(item) == isRemove) {
				if (begin < 0) {
					begin = index;
				}
				++removed;
				it.remove();
			} else {
				++retained;
				if (begin >= 0) {
					fireEvent(ListDataEvent.INTERVAL_REMOVED, begin, index - 1);
					index = begin; //this range removed, the index is reset to begin
					begin = -1;
				}
			}
		}
		if (begin >= 0) {
			fireEvent(ListDataEvent.INTERVAL_REMOVED, begin, index - 1);
		}
			
		return removed > 0;
	}
		
	public int size() {
		return _set.size();
	}
	
	public Object[] toArray() {
		return _set.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _set.toArray(a);
	}
}