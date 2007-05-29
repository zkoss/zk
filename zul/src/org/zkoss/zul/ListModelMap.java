/* ListModelMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 01 11:15:23     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;

/**
 * <p>This is the {@link ListModel} as a {@link java.util.Map} to be used with {@link Listbox}.
 * Add or remove the contents of this model as a List would cause the associated Listbox to change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelMap extends AbstractListModel
implements ListModelExt, Map, java.io.Serializable {
	protected Map _map; //(key, value)
	
	/**
	 * Creates an instance which accepts a "live" Map as its inner Map.
	 * <p>It is deprecated. Use {@link #ListModelMap(Map, boolean)} instead.
	 * @param map the inner Map storage.
	 * @deprecated
	 */
	public static ListModelMap instance(Map map) {
		return new ListModelMap(map, true);
	}

	/**
	 * Constructor.
	 *
	 * @param map the map to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified map.
	 * If false, the content of the specified map is copied.
	 * If true, this object is a 'facade' of the specified map,
	 * i.e., when you add or remove items from this {@link ListModelMap},
	 * the inner "live" map would be changed accordingly.
	 */
	public ListModelMap(Map map, boolean live) {
		_map = live ? map: new LinkedHashMap(map);
	}
	
	/**
	 * Constructor.
	 */
	public ListModelMap() {
		_map = new LinkedHashMap();
	}
	
	/**
	 * Constructor.
	 */
	public ListModelMap(Map map) {
		_map = new LinkedHashMap(map);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 */
	public ListModelMap(int initialCapacity) {
		_map = new LinkedHashMap(initialCapacity);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 * @param loadFactor the loadFactor to increase capacity of this ListModelMap.
	 */
	public ListModelMap(int initialCapacity, float loadFactor) {
		_map = new LinkedHashMap(initialCapacity, loadFactor);
	}

	/**
	 * Get the inner real Map.
	 */	
	public Map getInnerMap() {
		return _map;
	}
	
	//-- ListModel --//
	public int getSize() {
		return _map.size();
	}
	
	public Object getElementAt(int j) {
		if (j < 0 || j >= _map.size())
			throw new IndexOutOfBoundsException(""+j);

		for (Iterator it = _map.entrySet().iterator();;) {
			final Object o = it.next();
			if (--j < 0)
				return o;
		}
	}

	//-- Map --//
	public void clear() {
		int i2 = _map.size() - 1;
		if (i2 < 0) {
			return;
		}
		_map.clear();
		fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
	}

	public boolean containsKey(Object key) {
		return _map.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return _map.containsValue(value);
	}
	
	public Set entrySet() {
		return new MyEntrySet(_map.entrySet());
	}
    
	public boolean equals(Object o) {
		return _map.equals(o instanceof ListModelMap ? ((ListModelMap)o)._map: o);
	}
	public String toString() {
		return _map.toString();
	}
	
	public Object get(Object key){
		return _map.get(key);
	}

	public int hashCode() {
		return _map.hashCode();
	}
		
	public boolean isEmpty() {
		return _map.isEmpty();
	}
    
	public Set keySet() {
		return new MyKeySet(_map.keySet());
	}

	public Object put(Object key, Object o) {
		final Object ret;
		if (_map.containsKey(key)) {
			if(Objects.equals(o, _map.get(key))) {
				return o; //nothing changed
			}
			int index = indexOfKey(key);
			ret = _map.put(key, o);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
		} else {
			int i1 = _map.size();
			ret = _map.put(key, o);
			fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
		}
		return ret;
	}
	/** Returns the index of the specified object based on the key.
	 *
	 * @param o the key to look for
	 */
	public int indexOfKey(Object o) {
		int j = 0;
		for (Iterator it = _map.keySet().iterator(); it.hasNext(); ++j) {
			if (o.equals(it.next()))
				return j;
		}
		return -1;
	}
	/** Returns the index of the specified object based on the entry (Map.Entry).
	 *
	 * @param o the object to look for. It must be an instance of Map.Entry.
	 */
	public int indexOfEntry(Object o) {
		int j = 0;
		for (Iterator it = _map.entrySet().iterator(); it.hasNext(); ++j) {
			if (o.equals(it.next()))
				return j;
		}
		return -1;
	}

	public void putAll(Map c) {
		int sz = c.size();
		if (sz <= 0) {
			return;
		}
		if (c == _map) { //special case
			return;
		}
		
		List added = new ArrayList(c.size());
		for(Iterator it = c.entrySet().iterator(); it.hasNext();) {
			final Entry entry = (Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (_map.containsKey(key)) {
				put(key, val);
			} else {
				added.add(entry);
			}
		}
		
		for(Iterator it = added.iterator(); it.hasNext();) {
			final Entry entry = (Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			_map.put(key, val);
		}

		int len = added.size();
		if (len > 0) {
			fireEvent(ListDataEvent.INTERVAL_ADDED, sz, sz + len - 1);
		}
	}

	public Object remove(Object key) {
		if (_map.containsKey(key)) {
			int index = indexOfKey(key);
			Object ret = _map.remove(key);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return ret;
		}
		return null;
	}

	public int size() {
		return _map.size();
	}
	
	public Collection values() {
		return new MyCollection(_map.values());
	}

	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator cmpr, final boolean ascending) {
		final List copy = new ArrayList(_map.entrySet());
		Collections.sort(copy, cmpr);
		_map.clear();
		for(Iterator it = copy.iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			_map.put(entry.getKey(), entry.getValue());
		}
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	private boolean removePartial(Collection master, Collection c, boolean isRemove) {
		int sz = c.size();
		int removed = 0;
		int retained = 0;
		int index = 0;
		int begin = -1;
		for(final Iterator it = master.iterator(); 
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
	
	private class MyIterator implements Iterator {
		private Iterator _it;
		private int _index = -1;
		
		public MyIterator(Iterator inner) {
			_it = inner;
			_index = -1;
		}
		
		public boolean hasNext() {
			return _it.hasNext();
		}
		
		public Object next() {
			++_index;
			return _it.next();
		}
		
		public void remove() {
			if (_index >= 0) {
				_it.remove();
				fireEvent(ListDataEvent.INTERVAL_REMOVED, _index, _index);
			}
		}
	}

	/** Represents the key set.
	 */
	private class MyKeySet implements Set {
		private final Set _set;
		public MyKeySet(Set inner) {
			_set = inner;
		}
		
		public void clear() {
			int i2 = _set.size() - 1;
			if (i2 < 0) {
				return;
			}
			_set.clear();
			fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
		}
			
		public boolean remove(Object o) {
			int index = indexOf(o);
			boolean ret = _set.remove(o);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return ret;
		}
		protected int indexOf(Object o) {
			return indexOfKey(o);
		}
		
		public boolean removeAll(Collection c) {
			if (_set == c || this == c) { //special case
				clear();
				return true;
			}
			return removePartial(_set, c, true);
		}
	
		public boolean retainAll(Collection c) {
			if (_set == c || this == c) { //special case
				return false;
			}
			return removePartial(_set, c, false);
		}
		
		public Iterator iterator() {
			return new MyIterator(_set.iterator());
		}

		public boolean add(Object o) {
			throw new UnsupportedOperationException("add()");
		}
		
		public boolean addAll(Collection col) {
			throw new UnsupportedOperationException("addAll()");
		}

		public boolean contains(Object o) {
			return _set == null ? false : _set.contains(o);
		}
		
		public boolean containsAll(Collection c) {
			return _set == null ? false : _set.containsAll(c);
		}
		
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof MyKeySet) {
				return Objects.equals(((MyKeySet)o)._set, _set);
			} else {
				return Objects.equals(_set, o);
			}
		}
		
		public int hashCode(){
			return _set == null ? 0 : _set.hashCode();
		}
		
		public boolean isEmpty() {
			return _set == null ? true : _set.isEmpty();
		}

		public int size() {
			return _set == null ? 0 : _set.size();
		}
		
		public Object[] toArray() {
			return _set == null ? new Object[0] : _set.toArray();
		}
		
		public Object[] toArray(Object[] a) {
			return _set == null ? a : _set.toArray(a);
		}
	}
	private class MyEntrySet extends MyKeySet {
		private MyEntrySet(Set inner) {
			super(inner);
		}
		protected int indexOf(Object o) {
			return indexOfEntry(o);
		}
	}

	private class MyCollection implements Collection {
		private Collection _inner;
		
		public MyCollection(Collection inner) {
			_inner = inner;
		}

		public void clear() {
			int i2 = _inner.size() - 1;
			if (i2 < 0) {
				return;
			}
			_inner.clear();
			fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
		}
		
		private int indexOfAndRemove(Object o) {
			int j = 0;
			for(Iterator it = _inner.iterator(); it.hasNext();++j) {
				final Object val = it.next();
				if (Objects.equals(val, o)) {
					it.remove();
					return j;
				}
			}
			return -1;
		}
		
		public boolean remove(Object o) {
			int index = indexOfAndRemove(o);
			if (index < 0) {
				return false;
			}
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return true;
		}

		public boolean removeAll(Collection c) {
			if (_inner == c || this == c) { //special case
				clear();
				return true;
			}
			return removePartial(_inner, c, true);
		}
		
		public boolean retainAll(Collection c) {
			if (_inner == c || this == c) { //special case
				return false;
			}
			return removePartial(_inner, c, false);
		}
		
		public Iterator iterator() {
			return new MyIterator(_inner.iterator());
		}

		public boolean add(Object o) {
			throw new UnsupportedOperationException("add()");
		}
		
		public boolean addAll(Collection col) {
			throw new UnsupportedOperationException("addAll()");
		}

		public boolean contains(Object o) {
			return _inner == null ? false : _inner.contains(o);
		}
		
		public boolean containsAll(Collection c) {
			return _inner == null ? false : _inner.containsAll(c);
		}
		
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof MyCollection) {
				return Objects.equals(((MyCollection)o)._inner, _inner);
			} else {
				return Objects.equals(_inner, o);
			}
		}
		
		public int hashCode(){
			return _inner == null ? 0 : _inner.hashCode();
		}
		
		public boolean isEmpty() {
			return _inner == null ? true : _inner.isEmpty();
		}

		public int size() {
			return _inner == null ? 0 : _inner.size();
		}
		
		public Object[] toArray() {
			return _inner == null ? new Object[0] : _inner.toArray();
		}
		
		public Object[] toArray(Object[] a) {
			return _inner == null ? a : _inner.toArray(a);
		}
	}
}
