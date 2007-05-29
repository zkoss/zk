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
	protected List _list; //(entry pair)
	protected Map _map; //(key, value)
	protected transient Method _getEntry;
	
	/**
	 * Creates an instance which accepts a "live" Map as its inner Map.
	 * <p>It is deprecated. Use {@link #newInstance} instead.
	 * @param map the inner Map storage.
	 * @deprecated
	 */
	public static ListModelMap instance(Map map) {
		return newInstance(map);
	}
	/**
	 * Creates an instance which accepts a "live" Map as its inner Map.
	 * Any change to this ListModelMap will change to the passed in "live" Map.
	 * @param map the inner Map storage.
	 * @deprecated
	 */
	public static ListModelMap newInstance(Map map) {
		return new ListModelMap(map, 0);
	}

	/**
	 * <p>Constructor, unlike other Map implementation, the passed in Map is a "live" map inside
	 * this ListModelMap; i.e., when you add or remove items from this ListModelMap,
	 * the inner "live" map would be changed accordingly.</p>
	 * @param map the inner "live" map that would be added and/or removed accordingly
	 * when you add and/or remove item to this ListModelMap.
	 * @param dummy dummy argument to avoid confuse with consturctor {@link #ListModelMap(Map)}.
	 */
	protected ListModelMap(Map map, int dummy) {
		_map = map;
		_list = new ArrayList(map.entrySet());
		init();
	}
	
	/**
	 * Constructor.
	 */
	public ListModelMap() {
		_map = new LinkedHashMap();
		_list = new ArrayList();
		init();
	}
	
	/**
	 * Constructor.
	 */
	public ListModelMap(Map map) {
		_map = new LinkedHashMap(map);
		_list = new ArrayList(_map.entrySet());
		init();
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 */
	public ListModelMap(int initialCapacity) {
		_map = new LinkedHashMap(initialCapacity);
		_list = new ArrayList(initialCapacity);
		init();
	}
	
	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 * @param loadFactor the loadFactor to increase capacity of this ListModelMap.
	 */
	public ListModelMap(int initialCapacity, float loadFactor) {
		_map = new LinkedHashMap(initialCapacity, loadFactor);
		_list = new ArrayList(initialCapacity);
		init();
	}

	/**
	 * Get the inner real Map.
	 */	
	public Map getInnerMap() {
		return _map;
	}
	
	private void init() {
		try {
			_getEntry = Classes.getAnyMethod(_map.getClass(), "getEntry", new Class[] {Object.class});
		} catch (NoSuchMethodException	ex) {
			//ignore
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	/* package */ Map.Entry getEntry(Object key) {
		if (_getEntry != null) {
			//tricky, use reflection to call HashMap implementation.
			try {
				return (Map.Entry) _getEntry.invoke(_map, new Object[] {key});
			} catch (java.lang.reflect.InvocationTargetException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (java.lang.IllegalAccessException ex) {
				//ignore
				_getEntry = null; //set method to null to avoid exception
			}
		}
		
		//degrade to stupid
		for(final Iterator it = _map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getKey() == key) {
				return entry;
			}
		}
		return null;
	}
	
	//-- ListModel --//
	public int getSize() {
		return _map.size();
	}
	
	public Object getElementAt(int j) {
		return _list.get(j);
	}

	//-- Map --//
	public void clear() {
		int i2 = _map.size() - 1;
		if (i2 < 0) {
			return;
		}
		_list.clear();
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
		return new MySet(_map.entrySet(), true);
	}
    
	public boolean equals(Object o) {
		return _map.equals(o);
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
		return new MySet(_map.keySet(), false);
	}

	public Object put(Object key, Object o) {
		Object ret = null;
		if (_map.containsKey(key)) {
			if(Objects.equals(o, _map.get(key))) {
				return o; //nothing changed
			}
			final Entry oldentry = getEntry(key);
			int index = _list.indexOf(oldentry);
			ret = _map.put(key, o);
			final Entry newentry = getEntry(key);
			_list.set(index, newentry);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
		} else {
			int i1 = _map.size();
			ret = _map.put(key, o);
			final Entry realentry = getEntry(key);
			_list.add(realentry);
			fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
		}
		return ret;
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
			Entry realentry = getEntry(key);
			_list.add(realentry);
		}

		int len = added.size();
		if (len > 0) {
			fireEvent(ListDataEvent.INTERVAL_ADDED, sz, sz + len - 1);
		}
	}

	public Object remove(Object key) {
		if (_map.containsKey(key)) {
			final Entry entry = getEntry(key);
			int index = _list.indexOf(entry);
			_list.remove(index);
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
		Collections.sort(_list, cmpr);
		_map.clear();
		for(Iterator it = _list.iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			_map.put(entry.getKey(), entry.getValue());
		}
		_list.clear();
		_list.addAll(_map.entrySet());
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
				_list.remove(_index);
				_it.remove();
				fireEvent(ListDataEvent.INTERVAL_REMOVED, _index, _index);
			}
		}
	}

	private class MySet implements Set {
		private Set _set;
		private boolean _entrySet;
		public MySet(Set inner, boolean entrySet) {
			_set = inner;
			_entrySet = entrySet;
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
			
		public boolean remove(Object o) {
			final Entry entry = _entrySet ? ((Entry) o) : getEntry(o);
			int index = _list.indexOf(entry);
			_list.remove(index);
			boolean ret = _set.remove(o);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return ret;
		}
		
		public boolean removeAll(Collection c) {
			if (_set == c || this == c) { //special case
				clear();
				return true;
			}
			if (_entrySet) {
				_list.removeAll(c);
			} else {
				for(Iterator it = c.iterator(); it.hasNext();) {
					Object key = it.next();
					if (_set.contains(key)) {
						final Entry entry = getEntry(key);
						_list.remove(entry);
					}
				}
			}
			return removePartial(_set, c, true);
		}
	
		public boolean retainAll(Collection c) {
			if (_set == c || this == c) { //special case
				return false;
			}
			if (_entrySet) {
				_list.retainAll(c);
			} else {
				List newlist = new ArrayList(c.size());
				for(Iterator it = c.iterator(); it.hasNext();) {
					final Object key = it.next();
					if (_set.contains(key)) {
						final Entry entry = getEntry(key);
						newlist.add(entry);
					}
				}
				_list = newlist;
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
			if (o instanceof MySet) {
				return Objects.equals(((MySet)o)._set, _set);
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
			_list.clear();
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
			_list.remove(index);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			return true;
		}

		public boolean removeAll(Collection c) {
			if (_inner == c || this == c) { //special case
				clear();
				return true;
			}
			int j = 0;
			for(Iterator it = _inner.iterator(); it.hasNext();++j) {
				final Object val = it.next();
				if (c.contains(val)) {
					_list.remove(j--); //after remove _list is short on element, index must kept as is
				}
			}
			return removePartial(_inner, c, true);
		}
		
		public boolean retainAll(Collection c) {
			if (_inner == c || this == c) { //special case
				return false;
			}
			int j = 0;
			for(Iterator it = _inner.iterator(); it.hasNext();++j) {
				final Object val = it.next();
				if (!c.contains(val)) {
					_list.remove(j--);
				}
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

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}
}
