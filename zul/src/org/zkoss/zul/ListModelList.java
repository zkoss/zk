/* ListModelList.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 23 17:43:13     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.SortedSet;

/**
 * <p>This is the {@link ListModel} as a {@link java.util.List} to be used with {@link Listbox}.
 * Add or remove the contents of this model as a List would cause the associated Listbox to change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelList extends AbstractListModel
implements ListModelExt, List, java.io.Serializable {
	protected List _list;

	/**
	 * Constructor
	 *
	 * @param list the list to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 * If false, the content of the specified list is copied.
	 * If true, this object is a 'facade' of the specified list,
	 * i.e., when you add or remove items from this ListModelList,
	 * the inner "live" list would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>list</code>
	 * if it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to hanle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelList(List list, boolean live) {
		_list = live ? list: new ArrayList(list);
	}

	/**
	 * Constructor.
	 */
	public ListModelList() {
		_list = new ArrayList();
	}
	
	/**
	 * Constructor.
	 * It mades a copy of the specified collection (i.e., not live).
	 */
	public ListModelList(Collection c) {
		_list = new ArrayList(c);
	}
	/**
	 * Constructor.
	 * It mades a copy of the specified array (i.e., not live).
	 * @since 2.4.1
	 */
	public ListModelList(Object[] array) {
		_list = new ArrayList(array.length);
		for (int j = 0; j < array.length; ++j)
			_list.add(array[j]);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelList.
	 */
	public ListModelList(int initialCapacity) {
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

	/**
	 * Get the inner real List.
	 */	
	public List getInnerList() {
		return _list;
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
		return _list.equals(o instanceof ListModelList ? ((ListModelList)o)._list: o);
	}
	
	public Object get(int index){
		return _list.get(index);
	}

	public int hashCode() {
		return _list.hashCode();
	}
	public String toString() {
		return _list.toString();
	}

	public int indexOf(Object elem) {
		return _list.indexOf(elem);
	}
	
	public boolean isEmpty() {
		return _list.isEmpty();
	}
    
    public Iterator iterator() {
		return new Iterator() {
			private Iterator _it = _list.iterator();
			private Object _current = null;
			public boolean hasNext() {
				return _it.hasNext();
			}
			public Object next() {
				_current = _it.next();
				return _current;
			}
			public void remove() {
				final int index = indexOf(_current);
				_it.remove();
				fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			}
		};
    }
    
    public int lastIndexOf(Object elem) {
    	return _list.lastIndexOf(elem);
    }
	
	public ListIterator listIterator() {
		return _list.listIterator();
	}
	
	public ListIterator listIterator(final int index) {
		return new ListIterator() {
			private ListIterator _it = _list.listIterator(index);
			private Object _current = null;
			public boolean hasNext() {
				return _it.hasNext();
			}
			public Object next() {
				_current = _it.next();
				return _current;
			}
			public void remove() {
				final int index = _list.indexOf(_current);
				if (index >= 0) {
					_it.remove();
					fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
				}
			}
			public void add(Object arg0) {
				final int index = _it.nextIndex();
				_it.add(arg0);
				fireEvent(ListDataEvent.INTERVAL_ADDED, index, index);
			}
			public boolean hasPrevious() {
				return _it.hasPrevious();
			}
			public int nextIndex() {
				return _it.nextIndex();
			}
			public Object previous() {
				_current = _it.previous();
				return _current;
			}
			public int previousIndex() {
				return _it.previousIndex();
			}
			public void set(Object arg0) {
				final int index = _list.indexOf(_current);
				if (index >= 0) {
					_it.set(arg0);
					fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
				}
			}
		};
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
		//bug 2448987: sublist must be live
		return new ListModelList(list, true);
	}
	
	public Object[] toArray() {
		return _list.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _list.toArray(a);
	}

	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator cmpr, final boolean ascending) {
		Collections.sort(_list, cmpr);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}
}