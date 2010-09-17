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

import java.util.LinkedList;
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
public class ListModelMap<K, V> extends AbstractListModel<Map.Entry<K, V>>
implements ListModelExt<Map.Entry<K, V>>, Map<K, V>, java.io.Serializable {
	protected Map<K, V> _map;
	
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
	public ListModelMap(Map<K, V> map, boolean live) {
		_map = live ? map: new LinkedHashMap<K, V>(map);
	}
	
	/**
	 * Constructor.
	 */
	public ListModelMap() {
		_map = new LinkedHashMap<K, V>();
	}
	
	/**
	 * Constructor.
	 * It mades a copy of the specified map (i.e., not live).
	 */
	public ListModelMap(Map<? extends K, ? extends V> map) {
		_map = new LinkedHashMap<K, V>(map);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 */
	public ListModelMap(int initialCapacity) {
		_map = new LinkedHashMap<K, V>(initialCapacity);
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 * @param loadFactor the loadFactor to increase capacity of this ListModelMap.
	 */
	public ListModelMap(int initialCapacity, float loadFactor) {
		_map = new LinkedHashMap<K, V>(initialCapacity, loadFactor);
	}

	/**
	 * Get the inner real Map.
	 */	
	public Map<K, V> getInnerMap() {
		return _map;
	}
	
	//-- ListModel --//
	public int getSize() {
		return _map.size();
	}
	/**
	 * Returns the entry (Map.Entry) at the specified index.
	 */
	public Map.Entry<K, V> getElementAt(int j) {
		if (j < 0 || j >= _map.size())
			throw new IndexOutOfBoundsException(""+j);

		for (Iterator<Map.Entry<K, V>> it = _map.entrySet().iterator();;) {
			final Map.Entry<K, V> o = it.next();
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
	
	public Set<Map.Entry<K, V>> entrySet() {
		return new Entries(_map.entrySet());
	}
    
	public boolean equals(Object o) {
		return _map.equals(o instanceof ListModelMap ? ((ListModelMap)o)._map: o);
	}
	public String toString() {
		return _map.toString();
	}
	
	public V get(Object key){
		return _map.get(key);
	}

	public int hashCode() {
		return _map.hashCode();
	}
		
	public boolean isEmpty() {
		return _map.isEmpty();
	}

	public Set<K> keySet() {
		return new Keys(_map.keySet());
	}

	public V put(K key, V o) {
		final V ret;
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

	public void putAll(Map<? extends K, ? extends V> c) {
		if (c == _map) //special case
			return;

		if (_map instanceof LinkedHashMap) {
			int sz = c.size();
			if (sz <= 0) {
				return;
			}
			
			List<Map.Entry<? extends K, ? extends V>> added = new ArrayList<Map.Entry<? extends K, ? extends V>>(c.size());
			for(Map.Entry<? extends K, ? extends V> me: c.entrySet()) {
				K key = me.getKey();
				if (_map.containsKey(key)) {
					put(key, me.getValue());
				} else {
					added.add(me);
				}
			}
			
			for(Map.Entry<? extends K, ? extends V> me: added) {
				_map.put(me.getKey(), me.getValue());
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

	public V remove(Object key) {
		if (_map.containsKey(key)) {
			//bug #1819318 Problem while using SortedSet with Databinding
			V ret = null;
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
		for(Map.Entry<K, V> entry: getSelection()) {
			if (Objects.equals(key, entry.getKey())) {
				removeSelection(entry);
				return;
			}
		}
	}
	private void removeSelectionByValue(Object value) {
		for(Map.Entry<K, V> entry: getSelection()) {
			if (Objects.equals(value, entry.getValue())) {
				removeSelection(entry);
				return;
			}
		}
	}

	public int size() {
		return _map.size();
	}
	
	public Collection<V> values() {
		return new Values(_map.values());
	}

	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator<Map.Entry<K, V>> cmpr, final boolean ascending) {
		final List<Map.Entry<K, V>> copy = new ArrayList<Map.Entry<K, V>>(_map.entrySet());
		Collections.sort(copy, cmpr);
		_map.clear();
		for(Map.Entry<K, V> me: copy) {
			_map.put(me.getKey(), me.getValue());
		}
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	private boolean removePartial(Collection<?> master, Collection<?> c, boolean isRemove, boolean byKey, boolean byValue) {
		int sz = c.size();
		int removed = 0;
		int retained = 0;
		int index = 0;
		int begin = -1;
		for(final Iterator<?> it = master.iterator(); 
			it.hasNext() && (!isRemove || removed < sz) && (isRemove || retained < sz); ++index) {
			Object item = it.next();
			if (c.contains(item) == isRemove) {
				if (begin < 0) {
					begin = index;
				}
				++removed;
				if (byKey) removeSelectionByKey(item);
				else if (byValue) removeSelectionByValue(item);
				else removeSelection(item);
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
	
	private class MyIterator<E> implements Iterator<E> {
		private Iterator<E> _it;
		private E _current;
		private int _index = -1;
		
		public MyIterator(Iterator<E> inner) {
			_it = inner;
			_index = -1;
		}
		
		public boolean hasNext() {
			return _it.hasNext();
		}
		
		public E next() {
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

	private abstract class MySet<E> implements Set<E> {
		private final Set<E> _set;
		private final boolean _keyset;
		public MySet(Set<E> inner, boolean keyset) {
			_set = inner;
			_keyset = keyset;
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
		abstract protected int indexOf(Object o);
		
		public boolean removeAll(Collection<?> c) {
			if (_set == c || this == c) { //special case
				clear();
				return true;
			}

			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_set, c, true, _keyset, false);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				removeAllSelection(c);
				final boolean ret = _set.removeAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
	
		public boolean retainAll(Collection<?> c) {
			if (_set == c || this == c) { //special case
				return false;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_set, c, false, _keyset, false);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				retainAllSelection(c);
				final boolean ret = _set.retainAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
		
		public Iterator<E> iterator() {
			return new MyIterator<E>(_set.iterator());
		}

		public boolean add(E o) {
			throw new UnsupportedOperationException("add()");
		}
		
		public boolean addAll(Collection<? extends E> col) {
			throw new UnsupportedOperationException("addAll()");
		}

		public boolean contains(Object o) {
			return _set == null ? false : _set.contains(o);
		}
		
		public boolean containsAll(Collection<?> c) {
			return _set == null ? false : _set.containsAll(c);
		}
		

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o instanceof MySet) {
				return Objects.equals(((MySet)o)._set, _set);
			} else {
				return Objects.equals(_set, o);
			}
		}
		public int hashCode(){
			return Objects.hashCode(_set);
		}

		public boolean isEmpty() {
			return _set == null || _set.isEmpty();
		}

		public int size() {
			return _set == null ? 0 : _set.size();
		}
		
		public Object[] toArray() {
			return _set == null ? new Object[0] : _set.toArray();
		}
		
		public <T> T[] toArray(T[] a) {
			return _set == null ? a : _set.toArray(a);
		}
	}

	/** Represents the key set.
	 */
	private class Keys extends MySet<K> {
		public Keys(Set<K> inner) {
			super(inner, true);
		}
		
		protected int indexOf(Object o) {
			return indexOfKey(o);
		}
	}
	private class Entries extends MySet<Map.Entry<K, V>> {
		private Entries(Set<Map.Entry<K, V>> inner) {
			super(inner, false);
		}
		protected int indexOf(Object o) {
			return indexOf(o);
		}
	}

	private abstract class MyCol<E> implements Collection<E> {
		protected Collection<E> _col;
		
		public MyCol(Collection<E> col) {
			_col = col;
		}

		public Iterator<E> iterator() {
			return new MyIterator<E>(_col.iterator());
		}

		public boolean add(E o) {
			throw new UnsupportedOperationException("add()");
		}
		
		public boolean addAll(Collection<? extends E> col) {
			throw new UnsupportedOperationException("addAll()");
		}

		public boolean contains(Object o) {
			return _col == null ? false : _col.contains(o);
		}
		
		public boolean containsAll(Collection<?> c) {
			return _col == null ? false : _col.containsAll(c);
		}
		
		public int hashCode(){
			return _col == null ? 0 : _col.hashCode();
		}
		
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof MyCol) {
				return Objects.equals(((MyCol)o)._col, _col);
			} else {
				return Objects.equals(_col, o);
			}
		}

		public boolean isEmpty() {
			return _col == null ? true : _col.isEmpty();
		}

		public int size() {
			return _col == null ? 0 : _col.size();
		}
		
		public Object[] toArray() {
			return _col == null ? new Object[0] : _col.toArray();
		}
		
		public <T> T[] toArray(T[] a) {
			return _col == null ? a : _col.toArray(a);
		}
	}
	private class Values extends MyCol<V> {
		private Collection<V> _col;
		
		public Values(Collection<V> col) {
			super(col);
		}

		public void clear() {
			int i2 = _col.size() - 1;
			if (i2 < 0) {
				return;
			}
			clearSelection();
			_col.clear();
			fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
		}
		
		private int indexOfAndRemove(Object o) {
			int j = 0;
			for(Iterator it = _col.iterator(); it.hasNext();++j) {
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
				final boolean ret = _col.remove(o);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}

		public boolean removeAll(Collection<?> c) {
			if (_col == c || this == c) { //special case
				clearSelection();
				clear();
				return true;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_col, c, true, false, true);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				removeAllSelection(c);
				final boolean ret = _col.removeAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
		
		public boolean retainAll(Collection<?> c) {
			if (_col == c || this == c) { //special case
				return false;
			}
			//bug #1819318 Problem while using SortedSet with Databinding
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				return removePartial(_col, c, false, false, true);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				retainAllSelection(c);
				final boolean ret = _col.retainAll(c);
				if (ret) {
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
				return ret;
			}
		}
	}
}
