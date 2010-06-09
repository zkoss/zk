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
import java.util.SortedMap;
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
	 * Constructor.
	 *
	 * @param map the map to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified map.
	 * If false, the content of the specified map is copied.
	 * If true, this object is a 'facade' of the specified map,
	 * i.e., when you add or remove items from this {@link ListModelMap},
	 * the inner "live" map would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>map</code>
	 * if it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to hanle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
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
	 * It mades a copy of the specified map (i.e., not live).
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
	/**
	 * Returns the entry (Map.Entry) at the specified index.
	 */
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
		clearSelection();
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
			if (Objects.equals(o, _map.get(key))) {
				return o; //nothing changed
			}
			int index = indexOfKey(key);
			ret = _map.put(key, o);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
		} else {
			ret = _map.put(key, o);
			
			//After put, the position can change if not LinkedHashMap
			//bug #1819318 Problem while using SortedSet with Databinding
			//bug #1839634 Problem while using HashSet with Databinding
			if (_map instanceof LinkedHashMap) {
				//bug 1869614 Problem when switching from ListModelList to ListModelMap, 
				//java.lang.IndexOutOfBoundsException: 1, interval added index should be _map.size() - 1
				final int i1 = _map.size() - 1;
				fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
			} else if (_map instanceof SortedMap) {
				final int i1 = indexOfKey(key);
				fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
			} else {//bug #1839634, HashMap, not sure the iteration sequence 
					//of the HashMap, must resync
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
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
			if (Objects.equals(o, it.next()))
				return j;
		}
		return -1;
	}
	/** Returns the index of the specified object based on the entry
	 * (Map.Entry).
	 *
	 * @param o the object to look for. It must be an instance of Map.Entry.
	 */
	public int indexOf(Object o) {
		int j = 0;
		for (Iterator it = _map.entrySet().iterator(); it.hasNext(); ++j) {
			if (Objects.equals(o, it.next()))
				return j;
		}
		return -1;
	}

	public void putAll(Map c) {
		if (_map instanceof LinkedHashMap) {
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
		} else { //bug #1819318  Problem while using SortedSet with Databinding
				//bug #1839634 Problem while using HashSet with Databinding
			_map.putAll(c);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
		}
	}

	public Object remove(Object key) {
		if (_map.containsKey(key)) {
			//bug #1819318 Problem while using SortedSet with Databinding
			Object ret = null;
			removeSelectionByKey(key);
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				int index = indexOfKey(key);
				ret = _map.remove(key);
				fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				ret = _map.remove(key);
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
			return ret;
		}
		return null;
	}
	
	private void removeSelectionByKey(Object key) {
		for(final Iterator it = getSelection().iterator(); it.hasNext();) {
			final Map.Entry entry = (Map.Entry) it.next();
			if (key.equals(entry.getKey())) {
				removeSelection(entry);
				break;
			}
		}
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
				removeSelection(item);
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
		private Object _current;
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
			_current = _it.next(); 
			return _current;
		}
		
		public void remove() {
			if (_index >= 0) {
				//bug #1819318 Problem while using SortedSet with Databinding
				removeSelection(_current);
				_it.remove();
				if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
					fireEvent(ListDataEvent.INTERVAL_REMOVED, _index, _index);
				} else {
					//bug #1839634 Problem while using HashSet with Databinding
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
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
			clearSelection();
			_set.clear();
			fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
		}
			
		public boolean remove(Object o) {
			boolean ret = false;
			if (_set.contains(o)) {
				//bug #1819318 Problem while using SortedSet with Databinding
				removeSelection(o);
				if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
					final int index = indexOf(o);
					ret = _set.remove(o);
					fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
				} else { //bug #1839634 Problem while using HashSet with Databinding
					ret = _set.remove(o);
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
			}
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

			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_set, c, true);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				removeAllSelection(c);
				final boolean ret = _set.removeAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
	
		public boolean retainAll(Collection c) {
			if (_set == c || this == c) { //special case
				return false;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_set, c, false);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				retainAllSelection(c);
				final boolean ret = _set.retainAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
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
			return indexOf(o);
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
			clearSelection();
			_inner.clear();
			fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
		}
		
		private int indexOfAndRemove(Object o) {
			int j = 0;
			for(Iterator it = _inner.iterator(); it.hasNext();++j) {
				final Object val = it.next();
				if (Objects.equals(val, o)) {
					removeSelection(o);
					it.remove();
					return j;
				}
			}
			return -1;
		}
		
		public boolean remove(Object o) {
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				int index = indexOfAndRemove(o);
				if (index < 0) {
					return false;
				}
				fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
				return true;
			} else {
				removeSelection(o);
				final boolean ret = _inner.remove(o);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}

		public boolean removeAll(Collection c) {
			if (_inner == c || this == c) { //special case
				clearSelection();
				clear();
				return true;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_inner, c, true);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				removeAllSelection(c);
				final boolean ret = _inner.removeAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
		
		public boolean retainAll(Collection c) {
			if (_inner == c || this == c) { //special case
				return false;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_inner, c, false);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				retainAllSelection(c);
				final boolean ret = _inner.retainAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
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
