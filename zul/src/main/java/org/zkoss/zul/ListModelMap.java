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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

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
		implements Sortable<Map.Entry<K, V>>, Map<K, V>, java.io.Serializable {
	private static final long serialVersionUID = 20120206122736L;

	protected Map<K, V> _map;

	private Comparator<Map.Entry<K, V>> _sorting;

	private boolean _sortDir;

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
	 * since {@link Listbox} is not smart enough to handle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelMap(Map<K, V> map, boolean live) {
		_map = live ? map : new LinkedHashMap<K, V>(map);
	}

	/**
	 * Constructor.
	 */
	public ListModelMap() {
		_map = new LinkedHashMap<K, V>();
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified map (i.e., not live).
	 *
	 * <p>Notice that if the data is static or not shared, it is better to
	 * use <code>ListModelMap(map, true)</code> instead, since
	 * making a copy is slower.
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
	@SuppressWarnings("unchecked")
	public Map.Entry<K, V> getElementAt(int j) {
		if (j < 0 || j >= _map.size())
			throw new IndexOutOfBoundsException("" + j);

		for (Iterator<Map.Entry<K, V>> it = _map.entrySet().iterator();;) {
			final Map.Entry<K, V> o = it.next();
			if (--j < 0)
				return new Entry0(o);
		}
	}

	private static class Entry0<K, V> implements Map.Entry<K, V>, Serializable {
		private K _key;
		private V _value;
		private transient Map.Entry<K, V> _entry;

		Entry0(Map.Entry<K, V> entry) {
			_entry = entry;
		}

		public K getKey() {
			return _entry != null ? _entry.getKey() : _key;
		}

		public V getValue() {
			return _entry != null ? _entry.getValue() : _value;
		}

		public V setValue(V value) {
			V oldValue = _value;
			if (_entry != null) {
				oldValue = _entry.getValue();
				_entry.setValue(value);
			} else
				_value = value;
			return oldValue;
		}

		public final boolean equals(Object o) {
			if (_entry != null)
				return _entry.equals(o);

			if (!(o instanceof Map.Entry))
				return false;

			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}

		public final int hashCode() {
			if (_entry != null)
				return _entry.hashCode();
			return (_key == null ? 0 : _key.hashCode()) ^ (_value == null ? 0 : _value.hashCode());
		}

		public final String toString() {
			if (_entry != null)
				return _entry.toString();
			return getKey() + "=" + getValue();
		}

		private void writeObject(java.io.ObjectOutputStream s) throws IOException {
			s.defaultWriteObject();
			if (_entry != null) {
				s.writeObject(_entry.getKey());
				s.writeObject(_entry.getValue());
			} else {
				s.writeObject(_key);
				s.writeObject(_value);
			}
		}

		@SuppressWarnings("unchecked")
		private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
			s.defaultReadObject();
			_key = (K) s.readObject();
			_value = (V) s.readObject();
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
		if (this == o)
			return true;
		return _map.equals(o instanceof ListModelMap ? ((ListModelMap<?, ?>) o)._map : o);
	}

	public String toString() {
		return _map.toString();
	}

	public V get(Object key) {
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
			} else { //bug #1839634, HashMap, not sure the iteration sequence 
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
		for (Iterator<K> it = _map.keySet().iterator(); it.hasNext(); ++j) {
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
		for (Iterator<?> it = _map.entrySet().iterator(); it.hasNext(); ++j) {
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

			List<Map.Entry<? extends K, ? extends V>> added = new ArrayList<Map.Entry<? extends K, ? extends V>>(
					c.size());
			for (Map.Entry<? extends K, ? extends V> me : c.entrySet()) {
				K key = me.getKey();
				if (_map.containsKey(key)) {
					put(key, me.getValue());
				} else {
					added.add(me);
				}
			}

			for (Map.Entry<? extends K, ? extends V> me : added) {
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
		for (Map.Entry<K, V> entry : getSelection()) {
			if (Objects.equals(key, entry.getKey())) {
				removeFromSelection(entry);
				return;
			}
		}
	}

	private void removeSelectionByValue(Object value) {
		for (Map.Entry<K, V> entry : getSelection()) {
			if (Objects.equals(value, entry.getValue())) {
				removeFromSelection(entry);
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

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmpr to compare.
	 */
	public void sort(Comparator<Map.Entry<K, V>> cmpr, final boolean ascending) {
		final List<Map.Entry<K, V>> copy = new ArrayList<Map.Entry<K, V>>(_map.entrySet());
		_sorting = cmpr;
		_sortDir = ascending;
		try {
			Collections.sort(copy, cmpr);
		} catch (ClassCastException e) {
			throw new UiException("Unable to sort, maybe you should use FieldComparator, sort=\"auto(key)\" or sort=\"auto(value)\"", e);
		}
		_map.clear();
		for (Map.Entry<K, V> me : copy) {
			_map.put(me.getKey(), me.getValue());
		}
		fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	public void sort() {
		if (_sorting == null)
			throw new UiException("The sorting comparator is not assigned, please use sort(Comparator cmpr, final boolean ascending)");
		sort(_sorting, _sortDir);
	}

	public String getSortDirection(Comparator<Map.Entry<K, V>> cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	@SuppressWarnings("unchecked")
	private boolean removePartial(Collection<?> master, Collection<?> c, boolean isRemove, boolean byKey,
			boolean byValue) {
		int sz = c.size();
		int removed = 0;
		int retained = 0;
		int index = 0;
		int begin = -1;
		// B60-ZK-1126.zul
		// Remember the selections to be cleared
		List selected = new ArrayList();
		for (final Iterator<?> it = master.iterator(); it.hasNext() && (!isRemove || removed < sz)
				&& (isRemove || retained < sz); ++index) {
			Object item = it.next();
			if (c.contains(item) == isRemove) {
				if (begin < 0) {
					begin = index;
				}
				++removed;
				it.remove();
				// B60-ZK-1126.zul
				// Clear the selection later
				selected.add(item);
			} else {
				++retained;
				if (begin >= 0) {
					fireEvent(ListDataEvent.INTERVAL_REMOVED, begin, index - 1);
					index = begin; //this range removed, the index is reset to begin
					begin = -1;
				}
			}
		}
		// B60-ZK-1126.zul
		// Clear the selected items that were removed
		if (!selected.isEmpty()) {
			if (byKey) {
				for (Object item : selected) {
					removeSelectionByKey(item);
				}
			} else if (byValue) {
				for (Object item : selected) {
					removeSelectionByValue(item);
				}
			} else {
				for (Object item : selected) {
					removeFromSelection(item);
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
		private int _nextIndex;

		public MyIterator(Iterator<E> inner) {
			_it = inner;
		}

		public boolean hasNext() {
			return _it.hasNext();
		}

		public E next() {
			_current = _it.next();
			++_nextIndex;
			return _current;
		}

		public void remove() {
			_it.remove();
			//bug #1819318 Problem while using SortedSet with Databinding
			removeFromSelection(_current);
			--_nextIndex;
			if (_map instanceof LinkedHashMap || _map instanceof SortedMap) {
				fireEvent(ListDataEvent.INTERVAL_REMOVED, _nextIndex, _nextIndex);
			} else {
				//bug #1839634 Problem while using HashSet with Databinding
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
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
				removeFromSelection(o);
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

		protected abstract int indexOf(Object o);

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

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o instanceof MySet) {
				return Objects.equals(((MySet<?>) o)._set, _set);
			} else {
				return Objects.equals(_set, o);
			}
		}

		public int hashCode() {
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
			return ListModelMap.this.indexOf(o);
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

		public int hashCode() {
			return _col == null ? 0 : _col.hashCode();
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof MyCol) {
				return Objects.equals(((MyCol<?>) o)._col, _col);
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
			for (Iterator<?> it = _col.iterator(); it.hasNext(); ++j) {
				final Object val = it.next();
				if (Objects.equals(val, o)) {
					removeFromSelection(o);
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
				removeFromSelection(o);
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

	@SuppressWarnings("unchecked")
	public Object clone() {
		ListModelMap<K, V> clone = (ListModelMap<K, V>) super.clone();
		if (_map != null)
			clone._map = new LinkedHashMap<K, V>(_map);
		return clone;
	}

	protected void fireSelectionEvent(Entry<K, V> e) {
		fireEvent(ListDataEvent.SELECTION_CHANGED, indexOf(e), -1);
	}

	protected void writeSelection(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.writeInt(_selection.size());
		for (final Map.Entry<K, V> sel : _selection)
			s.writeObject(sel.getKey());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void readSelection(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		_selection = newEmptySelection();
		int size = s.readInt();
		while (--size >= 0) {
			((Set) _selection).add(s.readObject());
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		//the performance is bad, but no better algorithm
		@SuppressWarnings("rawtypes")
		Set selection = _selection;
		_selection = newEmptySelection();
		for (final Object key : selection) {
			if (_map.containsKey(key))
				for (Map.Entry<K, V> entry : _map.entrySet())
					if (Objects.equals(key, entry.getKey()))
						_selection.add(entry);
		}
	}
}
