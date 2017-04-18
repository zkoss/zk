/* AllocUtil.java

	Purpose:
		
	Description:
		
	History:
		2014/5/14 Created by henrichen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.List;

/**
 * For ZK-2289, Memory allocation utility.
 * @author henrichen
 * @since 7.0.3
 */
public class AllocUtil {
	public static AllocUtil inst = new AllocUtil();
	
	private final Interner<String> interner = new Interner<String>();
	
	/**
	 * Put key, value into the specified map.
	 * @param map the map to be put key, value in.
	 * @param key the key
	 * @param value the value
	 * @return the map
	 */
	public <K, V> Map<K, V> putMap(Map<K, V> map, K key, V value) {
		if (map == null) {
			map = new HashMap<K, V>(1);
		}
		map.put(key, value);
		return map;
	}
	
	/**
	 * Put key, value into the specified LinkedHashMap.
	 * @param map the LinkedHashMap to be put key, value in.
	 * @param key the key
	 * @param value the value
	 * @return the map
	 */
	public <K, V> Map<K, V> putLinkedHashMap(Map<K, V> map, K key, V value) {
		if (map == null) {
			map = new LinkedHashMap<K, V>(1);
		}
		map.put(key, value);
		return map;
	}
	
	/**
	 * Prepare a suitable LinkedHashMap that optimize the space.
	 */
	public <K, V> Map<K, V> newLinkedHashMap(int size) {
		return new LinkedHashMap<K, V>(size);
	}
	
	/**
	 * Add value into the specified set.
	 * @param set the set to be add value in
	 * @param value the value
	 * @return the set
	 */
	public <V> Set<V> addSet(Set<V> set, V value) {
		if (set == null) {
			set = new HashSet<V>();
		}
		set.add(value);
		return set;
	}
	
	public <V> Set<V> addLinkedHashSet(Set<V> set, V value) {
		if (set == null) {
			set = new LinkedHashSet<V>(1);
		}
		set.add(value);
		return set;
	}
	
	public <V> Set<V> addWeakIdentityHashSet(Set<V> set, V value) {
		if (set == null) {
			set =  new WeakIdentityMap<V, Boolean>().keySet();
		}
		set.add(value);
		return set;
	}
	
	/**
	 * Add value into the spcified list.
	 * @param list the list to be add value in
	 * @param value the value
	 * @return the list
	 */
	public <V> List<V> addList(List<V> list, V value) {
		if (list == null) {
			list = new ArrayList<V>(1);
		}
		list.add(value);
		return list;
	}
	
	/**
	 * Returns the processed script.
	 * @param script
	 */
	public Object processScript(Object script) {
		if (script instanceof String) {
			return interner.intern((String) script);
		}
		return script;
	}
	
	private static class Interner<E> {
		
		private final WeakHashMap<E, WeakReference<E>> map = new WeakHashMap<E, WeakReference<E>>(1024);
		
		public E intern(E value) {
			synchronized(map) {
				WeakReference<E> reference = map.get(value);
				if (reference != null) {
					E internedValue = reference.get();
					if (internedValue != null) {
						return internedValue;
					}
				}
			
				map.put(value, new WeakReference<E>(value));
			}
			
			return value;
		}
	}
}
