/* SmartListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 23 17:43:13     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import org.zkoss.lang.reflect.FacadeInvoker;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

/**
 * <p>This is the smart list model to be used with listbox. Add or remove the contents of 
 * this model would cause the associated listbox to change accordingly. Note that
 * this SmartListModel is also a java.util.List.</p>
 *
 * @author Henri
 */
public class SmartListModel extends AbstractListModel implements List {
	private List _list;
	
	/**
	 * Constructor.
	 */
	public SmartListModel() {
		_list = new ArrayList();
	}
	
	/**
	 * <p>Constructor, unlike other List implementation, the passed in List is a "live" list inside
	 * this SmartListModel; i.e., when you add or remove items from this SmartListModel,
	 * the inner "live" list would be changed accordingly.</p>
	 * @param list the inner "live" list that would be added and/or removed accordingly
	 * when you add and/or remove item to this SmartListModel.
	 */
	public SmartListModel(List list) {
		_list = list;
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this SmartListModel.
	 */
	public SmartListModel(int initialCapacity) {
		_list = new ArrayList(initialCapacity);
	}
	
	/**
	 * Remove from fromIndex(inclusive) to toIndex(exclusive). If fromIndex equals toIndex, 
	 * this methods do nothing.
	 * @param fromIndex the begin index (inclusive) to be removed.
	 * @param toIndex the end index (exclusive) to be removed.
	 */
	public void removeRange(int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new UiException("fromIndex must less than toIndex: fromIndex: "+fromIndex+", toIndex: "+toIndex);
		}
		if (fromIndex == toIndex) {
			return;
		}
		int sz = _list.size();
		if (sz == fromIndex) {
			return;
		}
		int index = fromIndex;
		for (final Iterator it = _list.listIterator(fromIndex); it.hasNext() && index < toIndex; ++index){
			it.next();
			it.remove();
		}
		fireEvent(ListDataEvent.INTERVAL_REMOVED, fromIndex, index - 1);
	}
	
	//-- ListModel --//
	public int getSize() {
		return _list.size();
	}
	
	public Object getElementAt(int j) {
		return _list.get(j);
	}

	//-- List --//
 	public void add(int index, Object element){
 		_list.add(index, element);
 		fireEvent(ListDataEvent.INTERVAL_ADDED, index, index);
 	}
 	
	public boolean add(Object o) {
		int i1 = _list.size();
		boolean ret = _list.add(o);
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
		return ret;
	}

	public boolean addAll(Collection c) {
		int sz = c.size();
		if (sz <= 0) {
			return false;
		}
		int i1 = _list.size();
		int i2 = i1 + sz - 1;
		boolean ret = _list.addAll(c);
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i2);
		return ret;
	}
	
	public boolean addAll(int index, Collection c) {
		int sz = c.size();
		if (sz <= 0) {
			return false;
		}
		int i2 = index + sz - 1;
		boolean ret = _list.addAll(index, c);
		fireEvent(ListDataEvent.INTERVAL_ADDED, index, i2);
		return ret;
	}
		
	public void clear() {
		int i2 = _list.size() - 1;
		if (i2 < 0) {
			return;
		}
		_list.clear();
		fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
	}
	
	public boolean contains(Object elem) {
		boolean ret = _list.contains(elem);
		return ret;
	}
	
	public boolean containsAll(Collection c) {
		return _list.containsAll(c);
	}
	
	public boolean equals(Object o) {
		return _list.equals(o);
	}
	
	public Object get(int index){
		return _list.get(index);
	}

	public int hashCode() {
		return _list.hashCode();
	}
		
	public int indexOf(Object elem) {
		return _list.indexOf(elem);
	}
	
	public boolean isEmpty() {
		return _list.isEmpty();
	}
    
    public Iterator iterator() {
    	return _list.iterator();
    }
    
    public int lastIndexOf(Object elem) {
    	return _list.lastIndexOf(elem);
    }
	
	public ListIterator listIterator() {
		return _list.listIterator();
	}
	
	public ListIterator listIterator(int index) {
		return _list.listIterator(index);
	}
	
	public Object remove(int index) {
		Object ret = _list.remove(index);
		fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
		return ret;
	}

	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			remove(index);
		}
		return false;
	}
	
	public boolean removeAll(Collection c) {
		if (_list == c || this == c) { //special case
			clear();
			return true;
		}
		return removePartial(c, true);
	}

	public boolean retainAll(Collection c) {
		if (_list == c || this == c) { //special case
			return false;
		}
		return removePartial(c, false);
	}
	
	private boolean removePartial(Collection c, boolean exclude) {
		boolean removed = false;
		int index = 0;
		int begin = -1;
		for(final Iterator it = _list.iterator(); it.hasNext(); ++index) {
			Object item = it.next();
			if (c.contains(item) == exclude) {
				if (begin < 0) {
					begin = index;
				}
				removed = true;
				it.remove();
			} else {
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
			
		return removed;
	}
		
	public Object set(int index, Object element) {
		Object ret = _list.set(index, element);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
		return ret;
	}
	
	public int size() {
		return _list.size();
	}

	public List subList(int fromIndex, int toIndex) {
		List list = _list.subList(fromIndex, toIndex);
		return new SmartListModel(list);
	}
	
	public Object[] toArray() {
		return _list.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _list.toArray(a);
	}
}