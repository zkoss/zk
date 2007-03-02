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

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collection;
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
public class ListModelMap extends AbstractListModel implements Map {
	protected List _list; //(key)
	protected Map _map; //(key, value)
	protected Method _getEntry;
	
	/**
	 * new an instance which accepts a "live" Map as its inner Map. Any change to this
	 * ListModelMap will change to the passed in "live" Map.
	 * @param map the inner Map storage.
	 */
	public static ListModelMap instance(Map map) {
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
		_list = new ArrayList(map.keySet());
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
		_list = new ArrayList(_map.keySet());
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
		return getEntry(getKey(j));
	}

	private Object getKey(int j) {
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
		return _map.entrySet();
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
		return _map.keySet();
	}

	public Object put(Object key, Object o) {
		int i1 = _map.size();
		Object ret = _map.put(key, o);
		_list.add(key);
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
		return ret;
	}

	public void putAll(Map c) {
		int sz = c.size();
		if (sz <= 0) {
			return;
		}
		int i1 = _map.size();
		int i2 = i1 + sz - 1;
		_map.putAll(c);
		_list.addAll(c.keySet());
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i2);
	}

	public Object remove(Object key) {
		if (_map.containsKey(key)) {
			int index = _list.indexOf(key);
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
		return _map.values();
	}
}
